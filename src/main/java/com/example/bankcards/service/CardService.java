package com.example.bankcards.service;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferHistoryRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardEncryptor cardEncryptor;
    private final CardNumberGenerator cardNumberGenerator;
    private final TransferHistoryRepository transferHistoryRepository;

    public CardResponse createCard(CreateCardRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(EntityNotFoundException::new);
        if (user.getRole() == RoleType.ROLE_ADMIN) {
            log.warn("Попытка создать карту для администратора с ID {}", user.getId());
            throw new AdminCardCreationException("Создать карту может только администратор");
        }

        String rawCardNumber = cardNumberGenerator.generate();
        String encryptedCardNumber = cardEncryptor.encrypt(rawCardNumber);

        Card card = Card.builder()
                .user(user)
                .encryptedNumber(encryptedCardNumber)
                .expirationDate(request.getExpirationDate())
                .status(CardStatus.ACTIVE)
                .balance(request.getBalance())
                .build();
        Card savedCard = cardRepository.save(card);
        log.info("Карта успешно создана с ID {}", savedCard.getId());

        return new CardResponse(
                savedCard.getId(),
                maskCardNumber(rawCardNumber),
                user.getFullName(),
                savedCard.getExpirationDate(),
                savedCard.getStatus().name(),
                savedCard.getBalance()
        );
    }

    private String maskCardNumber(String number) {
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    public void changeStatus(Long id, ChangeStatusRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта не с " + id + " найдена"));
        if (card.getStatus() == CardStatus.EXPIRED && request.getStatus() == CardStatus.ACTIVE) {
            log.warn("Попытка активировать просроченную карту ID {}", id);
            throw new InvalidCardStatusChangeException("Невозможно активировать просроченную карту");
        }
        card.setStatus(request.getStatus());
        cardRepository.save(card);
        log.info("Статус карты ID {} успешно изменен на {}", id, request.getStatus());
    }

    public void delete(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
        cardRepository.delete(card);
        log.info("Карта с ID {} успешно удалена", id);
    }

    public Page<CardResponse> getAllCards(Pageable pageable) {
        Page<Card> cards = cardRepository.findAll(pageable);

        return cards.map(this::mapToCardResponse);
    }

    private CardResponse mapToCardResponse(Card card) {
        String masked = maskEncryptedCardNumber(card.getEncryptedNumber());

        return new CardResponse(
                card.getId(),
                masked,
                card.getUser().getFullName(),
                card.getExpirationDate(),
                card.getStatus().name(),
                card.getBalance()
        );
    }
    private String maskEncryptedCardNumber(String encryptedNumber) {
        try {
            String decrypted = cardEncryptor.decrypt(encryptedNumber);
            return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
        } catch (Exception e) {
            return "**** **** **** ????";
        }
    }

    public Page<CardResponse> getUserCards(String username, Optional<String> status, Pageable pageable) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("Пользователь" + username + " не найден"));

        Page<Card> cards;

        if (status.isPresent()) {
            CardStatus cardStatus;
            try {
                cardStatus = CardStatus.valueOf(status.get().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Неверный статус карты: {}", status.get());
                throw new InvalidCardStatusException("Неверный статус карты: " + status.get());
            }

            cards = cardRepository.findByUserAndStatus(user, cardStatus, pageable);
        } else {
            cards = cardRepository.findByUser(user, pageable);
        }

        return cards.map(this::mapToCardResponse);
    }

    public BigDecimal getCardBalance(Long id, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + username));

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта с ID не найдена"));

        if (!card.getUser().getId().equals(user.getId())) {
            log.warn("Попытка доступа к чужой карте ID {} пользователем {}", id, username);
            throw new ForbiddenOperationException("У вас нет доступа к балансу этой карты");
        }

        return card.getBalance();
    }

    @Transactional
    public void transferBetweenCards(TransferRequest request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + username + " не найден"));

        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException("Карта отправителя не найдена"));

        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new CardNotFoundException("Карта получателя не найдена"));

        if (!fromCard.getUser().getId().equals(user.getId()) || !toCard.getUser().getId().equals(user.getId())) {
            log.warn("Попытка перевода между чужими картами пользователем {}", username);
            throw new ForbiddenOperationException("Вы можете переводить только между своими картами");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStateException("Обе карты должны быть активны для перевода");
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InvalidCardStateException("Недостаточно средств на карте отправителя");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        TransferHistory history = new TransferHistory();
        history.setSenderCard(fromCard);
        history.setReceiverCard(toCard);
        history.setAmount(request.getAmount());
        history.setTransferredAt(LocalDateTime.now());

        transferHistoryRepository.save(history);
        log.info("Перевод завершен. История перевода ID: {}", history.getId());
    }

    public CardResponse getCardById(Long id, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
        if (!card.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("У вас нет доступа к этой карте");
        }

        return mapToCardResponse(card);
    }

    public List<TransferHistoryResponse> getAllTransfer() {
        log.info("Получение всей истории переводов");
        return transferHistoryRepository.findAll().stream()
                .map(t -> new TransferHistoryResponse(
                        t.getId(),
                        t.getSenderCard().getId(),
                        t.getReceiverCard().getId(),
                        t.getAmount(),
                        t.getTransferredAt()
                ))
                .toList();
    }

    public List<TransferHistoryResponse> getTransferByUserId(Long userId) {
        log.info("Получение истории переводов для пользователя ID {}", userId);
            return transferHistoryRepository.findAllByUserId(userId).stream()
                    .map(t -> new TransferHistoryResponse(
                            t.getId(),
                            t.getSenderCard().getId(),
                            t.getReceiverCard().getId(),
                            t.getAmount(),
                            t.getTransferredAt()
                    ))
                    .toList();
    }

    public ResponseEntity<List<TransferHistoryResponse>> getAllTransferUser(String email, Pageable pageable) {
        log.info("Получение истории переводов для email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Page<Card> cardPage = cardRepository.findByUser(user, pageable);
        List<Card> cards = cardPage.getContent();

        if (cards.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<TransferHistory> transfers = transferHistoryRepository.findBySenderOrReceiverCardIds(cards);

        List<TransferHistoryResponse> responses = transfers.stream()
                .map(t -> new TransferHistoryResponse(
                        t.getId(),
                        t.getSenderCard().getId(),
                        t.getReceiverCard().getId(),
                        t.getAmount(),
                        t.getTransferredAt()
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }
}

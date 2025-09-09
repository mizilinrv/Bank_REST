package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.dto.card.ChangeStatusRequest;
import com.example.bankcards.dto.card.TransferRequest;
import com.example.bankcards.dto.card.TransferHistoryResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.RoleType;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransferHistory;
import com.example.bankcards.exception.AdminCardCreationException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InvalidCardStatusChangeException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.InvalidCardStateException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferHistoryRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing bank cards.
 * <p>
 * Provides functionality for:
 * <ul>
 *     <li>Creating cards</li>
 *     <li>Changing card status</li>
 *     <li>Deleting cards</li>
 *     <li>Retrieving all cards and user's cards</li>
 *     <li>Getting card balance</li>
 *     <li>Transferring between cards</li>
 *     <li>Retrieving transfer history</li>
 * </ul>
 * <p>
 * Uses {@link CardRepository}, {@link UserRepository}, {@link CardEncryptor},
 * {@link CardNumberGenerator}, and {@link TransferHistoryRepository}.
 */
@Service
@AllArgsConstructor
@Slf4j
public class CardService {

    /**
     * Repository for performing CRUD operations on {@link Card} entities.
     */
    private final CardRepository cardRepository;

    /**
     * Repository for performing CRUD operations on {@link User} entities.
     */
    private final UserRepository userRepository;

    /**
     * Component responsible for encrypting and decrypting card numbers.
     */
    private final CardEncryptor cardEncryptor;

    /**
     * Component responsible for generating unique card numbers.
     */
    private final CardNumberGenerator cardNumberGenerator;

    /**
     * Repository for storing and retrieving {@link TransferHistory} entities.
     */
    private final TransferHistoryRepository transferHistoryRepository;

    /**
     * Number of last digits displayed when masking the card number.
     */
    private static final int LAST_DIGITS_COUNT = 4;

    /**
     * Creates a new card for the specified user.
     *
     * @param request data for creating the card
     * @return {@link CardResponse} with information about the created card
     * @throws EntityNotFoundException if the user is not found
     * @throws AdminCardCreationException
     * if trying to create a card for an admin
     */
    public CardResponse createCard(final CreateCardRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(EntityNotFoundException::new);
        if (user.getRole() == RoleType.ROLE_ADMIN) {
            log.warn(
                    "Attempt to create card for admin with ID {}",
                    user.getId()
            );
            throw new AdminCardCreationException(
                    "Only non-admin users can have cards created"
            );
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
        log.info("Card successfully created with ID {}", savedCard.getId());

        return new CardResponse(
                savedCard.getId(),
                maskCardNumber(rawCardNumber),
                user.getFullName(),
                savedCard.getExpirationDate(),
                savedCard.getStatus().name(),
                savedCard.getBalance()
        );
    }

    /**
     * Masks the card number, keeping only the last
     * {@link #LAST_DIGITS_COUNT} digits visible.
     *
     * @param number card number
     * @return masked card number
     */
    private String maskCardNumber(final String number) {
        return "**** **** **** " + number.substring(
                number.length() - LAST_DIGITS_COUNT
        );
    }

    /**
     * Changes the status of a card.
     *
     * @param id card ID
     * @param request new card status
     * @throws CardNotFoundException if the card is not found
     * @throws InvalidCardStatusChangeException
     * if trying to activate an expired card
     */
    public void changeStatus(final Long id, final ChangeStatusRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card with ID " + id + " not found")
                );
        if (card.getStatus() == CardStatus.EXPIRED
                && request.getStatus() == CardStatus.ACTIVE) {
            log.warn("Attempt to activate expired card ID {}", id);
            throw new InvalidCardStatusChangeException(
                    "Cannot activate an expired card"
            );
        }
        card.setStatus(request.getStatus());
        cardRepository.save(card);
        log.info(
                "Card status ID {} successfully changed to {}",
                id,
                request.getStatus()
        );
    }

    /**
     * Deletes a card by its ID.
     *
     * @param id card ID
     * @throws CardNotFoundException if the card is not found
     */
    public void delete(final Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        cardRepository.delete(card);
        log.info("Card with ID {} successfully deleted", id);
    }

    /**
     * Retrieves a page of all cards.
     *
     * @param pageable pagination information
     * @return page of {@link CardResponse}
     */
    public Page<CardResponse> getAllCards(final Pageable pageable) {
        Page<Card> cards = cardRepository.findAll(pageable);
        return cards.map(this::mapToCardResponse);
    }

    private CardResponse mapToCardResponse(final Card card) {
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

    private String maskEncryptedCardNumber(final String encryptedNumber) {
        try {
            String decrypted = cardEncryptor.decrypt(encryptedNumber);
            return "**** **** **** " + decrypted.substring(
                    decrypted.length() - LAST_DIGITS_COUNT);
        } catch (Exception e) {
            return "**** **** **** ????";
        }
    }

    /**
     * Retrieves a list of cards for a specific user,
     * optionally filtered by status.
     *
     * @param username user's email
     * @param status optional card status
     * @param pageable pagination information
     * @return page of {@link CardResponse}
     * @throws NotFoundException if the user is not found
     * @throws InvalidCardStatusException if an invalid status is provided
     */
    public Page<CardResponse> getUserCards(
            final String username,
            final Optional<String> status,
            final Pageable pageable
    ) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException(
                        "User " + username + " not found"));

        Page<Card> cards;
        if (status.isPresent()) {
            CardStatus cardStatus;
            try {
                cardStatus = CardStatus.valueOf(status.get().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid card status: {}", status.get());
                throw new InvalidCardStatusException(
                        "Invalid card status: " + status.get()
                );
            }
            cards = cardRepository.findByUserAndStatus(
                    user,
                    cardStatus,
                    pageable
            );
        } else {
            cards = cardRepository.findByUser(user, pageable);
        }
        return cards.map(this::mapToCardResponse);
    }

    /**
     * Retrieves the balance of a card for a specific user.
     *
     * @param id card ID
     * @param username user's email
     * @return card balance
     * @throws NotFoundException if the user is not found
     * @throws CardNotFoundException if the card is not found
     * @throws ForbiddenOperationException
     * if the user tries to access someone else's card
     */
    public BigDecimal getCardBalance(final Long id, final String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException(
                        "User not found: " + username)
                );

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card with ID not found")
                );

        if (!card.getUser().getId().equals(user.getId())) {
            log.warn(
                    "Attempt to access another user's card ID {} by {}",
                    id,
                    username
            );
            throw new ForbiddenOperationException(
                    "No access to this card's balance"
            );
        }

        return card.getBalance();
    }

    /**
     * Performs a transfer between two cards belonging to the same user.
     *
     * @param request transfer data
     * @param username user's email
     * @throws NotFoundException if the user is not found
     * @throws CardNotFoundException if one of the cards is not found
     * @throws ForbiddenOperationException
     * if the cards belong to different users
     * @throws InvalidCardStateException
     * if cards are inactive or insufficient funds
     */
    @Transactional
    public void transferBetweenCards(
            final TransferRequest request,
            final String username
    ) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException(
                        "User with email " + username + " not found")
                );
        Card fromCard =
                cardRepository.findByIdForUpdate(request.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException(
                        "Sender card not found")
                );
        Card toCard = cardRepository.findByIdForUpdate(request.getToCardId())
                .orElseThrow(() -> new CardNotFoundException(
                        "Receiver card not found")
                );

        if (!fromCard.getUser().getId().equals(user.getId())
                || !toCard.getUser().getId().equals(user.getId())) {
            log.warn(
                    "Attempted transfer between other users' cards by {}",
                    username
            );
            throw new ForbiddenOperationException(
                    "You can only transfer between your own cards"
            );
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE
                || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStateException(
                    "Both cards must be active for transfer"
            );
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InvalidCardStateException(
                    "Insufficient funds on sender's card"
            );
        }

        fromCard.setBalance(
                fromCard.getBalance().subtract(request.getAmount())
        );
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        TransferHistory history = new TransferHistory();
        history.setSenderCard(fromCard);
        history.setReceiverCard(toCard);
        history.setAmount(request.getAmount());
        history.setTransferredAt(LocalDateTime.now());

        transferHistoryRepository.save(history);
        log.info(
                "Transfer completed. Transfer history ID: {}", history.getId()
        );
    }

    /**
     * Retrieves a card by ID and checks if it belongs to the user.
     *
     * @param id card ID
     * @param username user's email
     * @return {@link CardResponse} with card information
     * @throws NotFoundException if the user is not found
     * @throws CardNotFoundException if the card is not found
     * @throws ForbiddenOperationException
     * if the card does not belong to the user
     */
    public CardResponse getCardById(final Long id, final String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        if (!card.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("No access to this card");
        }

        return mapToCardResponse(card);
    }

    /**
     * Retrieves the full transfer history.
     *
     * @return list of {@link TransferHistoryResponse}
     */
    public List<TransferHistoryResponse> getAllTransfer() {
        log.info("Retrieving full transfer history");
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

    /**
     * Retrieves transfer history for a specific user by user ID.
     *
     * @param userId user ID
     * @return list of {@link TransferHistoryResponse}
     */
    public List<TransferHistoryResponse> getTransferByUserId(
            final Long userId) {
        log.info("Retrieving transfer history for user ID {}", userId);
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

    /**
     * Retrieves a user's transfer history by email with pagination.
     *
     * @param email user's email
     * @param pageable pagination information
     * @return {@link ResponseEntity}
     * containing a list of {@link TransferHistoryResponse}
     * @throws NotFoundException if the user is not found
     */
    public ResponseEntity<List<TransferHistoryResponse>> getAllTransferUser(
            final String email, final Pageable pageable) {
        log.info("Retrieving transfer history for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Page<Card> cardPage = cardRepository.findByUser(user, pageable);
        List<Card> cards = cardPage.getContent();

        if (cards.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<TransferHistory> transfers =
                transferHistoryRepository.findBySenderOrReceiverCardIds(cards);

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

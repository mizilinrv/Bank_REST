package com.example.bankcards.service;

import com.example.bankcards.dto.card.BlockResponse;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BlockRequestNotFoundException;
import com.example.bankcards.exception.ForbiddenOperationException;
import com.example.bankcards.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.BlockRequestRepository;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BlockRequestService {

    private final CardRepository cardRepository;
    private final BlockRequestRepository blockRequestRepository;


    public void createRequest(Long cardId, String userEmail) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Карта с ID {} не найдена", cardId);
                    return new NotFoundException("Карта с ID " + cardId + " не найдена");
                });


        if (!card.getUser().getEmail().equals(userEmail)) {
            log.warn("Попытка блокировки чужой карты. Владелец: {}, Запросивший: {}", card.getUser().getEmail(), userEmail);
            throw new ForbiddenOperationException("Нельзя запрашивать блокировку чужой карты");
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            log.warn("Карта уже заблокирована. ID: {}", cardId);
            throw new ForbiddenOperationException("Карта уже заблокирована");
        }

        if (card.getStatus() == CardStatus.EXPIRED) {
            log.warn("Срок действия карты истек. ID: {}", cardId);
            throw new ForbiddenOperationException("Срок действия карты истек");
        }

        User user = card.getUser();

        boolean exists = blockRequestRepository.existsByUserAndCard(user, card);
        if (exists) {
            log.warn("Запрос на блокировку уже существует. userId: {}, cardId: {}", user.getId(), card.getId());
            throw new ForbiddenOperationException("Запрос на блокировку этой карты уже существует");
        }

        BlockRequest request = new BlockRequest();
        request.setUser(user);
        request.setCard(card);
        blockRequestRepository.save(request);

        log.info("Запрос на блокировку карты создан. userId: {}, cardId: {}", user.getId(), card.getId());
    }

    public List<BlockResponse> getAllPendingRequests() {
        List<BlockRequest> blockRequests = blockRequestRepository.findByProcessedFalse();
        log.info("Найдено {} необработанных запросов", blockRequests.size());
        return blockRequests.stream()
                .map(request -> new BlockResponse(
                        request.getId(),
                        request.getUser().getId(),
                        request.getCard().getId(),
                        request.getRequestedAt()
                ))
                .toList();
    }

    public void processBlockRequest(Long requestId) throws EntityNotFoundException {
        BlockRequest request = blockRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос на блокировку с ID {} не найден", requestId);
                    return new BlockRequestNotFoundException("Не найден запрос на блокировку");
                });

        Card card = request.getCard();
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        log.info("Карта заблокирована. cardId: {}", card.getId());

        request.setProcessed(true);
        blockRequestRepository.save(request);
        log.info("Запрос на блокировку отмечен как обработанный. requestId: {}", requestId);
    }
}

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


/**
 * Service responsible for handling block requests for user cards.
 * <p>
 * This service allows creating, retrieving, and processing block requests.
 * It ensures that only the card owner can request a block and that the
 * card is in a valid state for blocking.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlockRequestService {

    /** Repository for accessing card entities. */
    private final CardRepository cardRepository;

    /** Repository for accessing block request entities. */
    private final BlockRequestRepository blockRequestRepository;

    /**
     * Creates a block request for a specific card.
     *
     * @param cardId    the ID of the card to block
     * @param userEmail the email of the user requesting the block
     * @throws NotFoundException if the card with the given ID does not exist
     * @throws ForbiddenOperationException if the user
     * is not the owner of the card
     *         or the card is already blocked or expired,
     *         or a block request already exists
     */
    public void createRequest(final Long cardId, final String userEmail) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card with ID {} not found", cardId);
                    return new NotFoundException(
                            "Card with ID " + cardId + " not found");
                });

        if (!card.getUser().getEmail().equals(userEmail)) {
            log.warn(
                    "Attempt to block someone else's card."
                            + " Owner: {}, Requester: {}",
                    card.getUser().getEmail(), userEmail
            );
            throw new ForbiddenOperationException(
                    "Cannot request block for another user's card"
            );
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            log.warn("Card already blocked. ID: {}", cardId);
            throw new ForbiddenOperationException("Card is already blocked");
        }

        if (card.getStatus() == CardStatus.EXPIRED) {
            log.warn("Card has expired. ID: {}", cardId);
            throw new ForbiddenOperationException("Card has expired");
        }

        User user = card.getUser();

        boolean exists = blockRequestRepository.existsByUserAndCard(user, card);
        if (exists) {
            log.warn(
                    "Block request already exists. userId: {}, cardId: {}",
                    user.getId(),
                    card.getId()
            );
            throw new ForbiddenOperationException(
                    "A block request for this card already exists"
            );
        }

        BlockRequest request = new BlockRequest();
        request.setUser(user);
        request.setCard(card);
        blockRequestRepository.save(request);

        log.info(
                "Block request created. userId: {}, cardId: {}",
                user.getId(),
                card.getId());
    }

    /**
     * Retrieves all pending (unprocessed) block requests.
     *
     * @return a list of {@link BlockResponse}
     * objects representing pending requests
     */
    public List<BlockResponse> getAllPendingRequests() {
        List<BlockRequest> blockRequests =
                blockRequestRepository.findByProcessedFalse();
        log.info("Found {} unprocessed block requests", blockRequests.size());
        return blockRequests.stream()
                .map(request -> new BlockResponse(
                        request.getId(),
                        request.getUser().getId(),
                        request.getCard().getId(),
                        request.getRequestedAt()
                ))
                .toList();
    }

    /**
     * Processes a block request by setting the card status to BLOCKED
     * and marking the request as processed.
     *
     * @param requestId the ID of the block request to process
     * @throws BlockRequestNotFoundException
     * if the block request with the given ID does not exist
     */
    public void processBlockRequest(final Long requestId)
            throws EntityNotFoundException {
        BlockRequest request = blockRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Block request with ID {} not found", requestId);
                    return new BlockRequestNotFoundException(
                            "Block request not found"
                    );
                });

        Card card = request.getCard();
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        log.info("Card blocked. cardId: {}", card.getId());

        request.setProcessed(true);
        blockRequestRepository.save(request);
        log.info("Block request marked as processed. requestId: {}", requestId);
    }
}

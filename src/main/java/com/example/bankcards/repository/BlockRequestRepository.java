package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing {@link BlockRequest} entities.
 * Provides CRUD operations and custom queries for block requests.
 */
public interface BlockRequestRepository
        extends JpaRepository<BlockRequest, Long> {

    /**
     * Retrieves all block requests that have not been processed yet.
     *
     * @return a list of unprocessed {@link BlockRequest} entities
     */
    List<BlockRequest> findByProcessedFalse();

    /**
     * Checks if a block request exists for the given user and card.
     *
     * @param user the {@link User} who requested the block
     * @param card the {@link Card} that is requested to be blocked
     * @return {@code true} if a block request exists for
     * the given user and card, {@code false} otherwise
     */
    boolean existsByUserAndCard(User user, Card card);
}

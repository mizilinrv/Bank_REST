package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Card} entities.
 * Provides CRUD operations and custom queries
 * for managing cards in the database.
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Retrieves a paginated list of cards belonging to a specific user.
     *
     * @param user the user whose cards are to be retrieved
     * @param pageable the pagination information
     * @return a page of {@link Card} entities for the given user
     */
    Page<Card> findByUser(User user, Pageable pageable);

    /**
     * Retrieves a paginated list of cards belonging
     * to a specific user with a specific status.
     *
     * @param user the user whose cards are to be retrieved
     * @param status the status of the cards to filter
     * @param pageable the pagination information
     * @return a page of {@link Card} entities
     * for the given user and status
     */
    Page<Card> findByUserAndStatus(
            User user,
            CardStatus status,
            Pageable pageable
    );
}

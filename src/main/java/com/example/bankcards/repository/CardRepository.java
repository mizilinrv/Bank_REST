package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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


    /**
     * Finds a {@link Card} entity by its ID using a pessimistic write lock.
     * <p>
     * This method locks the selected card row in the database to prevent
     * concurrent modifications by other transactions. It is typically used
     * in scenarios like transferring funds between cards to ensure that
     * the balance cannot be updated simultaneously by multiple threads.
     * </p>
     *
     * @param id the ID of the card to retrieve
     * @return an {@link Optional} containing
     * the {@link Card} if found, or empty if not found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.id = :id")
    Optional<Card> findByIdForUpdate(@Param("id") Long id);
}

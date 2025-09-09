package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.TransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for {@link TransferHistory} entities.
 * Provides CRUD operations and custom queries
 * for managing transfer history records.
 */
public interface TransferHistoryRepository
        extends JpaRepository<TransferHistory, Long> {

    /**
     * Retrieves all transfer records where the sender or
     * receiver card is in the specified list of cards.
     *
     * @param cardIds the list of cards to check for transfers
     * @return a list of {@link TransferHistory}
     * entities involving the given cards
     */
    @Query("SELECT t FROM TransferHistory t WHERE"
            + " t.senderCard IN :cardIds OR t.receiverCard IN :cardIds")
    List<TransferHistory> findBySenderOrReceiverCardIds(
            @Param("cardIds") List<Card> cardIds);

    /**
     * Retrieves all transfer records associated with
     * a specific user, either as sender or receiver.
     *
     * @param userId the ID of the user
     * @return a list of {@link TransferHistory} entities for the given user
     */
    @Query("SELECT t FROM TransferHistory t WHERE"
            + " t.senderCard.user.id = :userId "
            + "OR t.receiverCard.user.id = :userId")
    List<TransferHistory> findAllByUserId(
            @Param("userId") Long userId);
}

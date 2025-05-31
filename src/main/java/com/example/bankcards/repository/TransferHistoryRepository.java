package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.TransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    @Query("SELECT t FROM TransferHistory t WHERE t.senderCard IN :cardIds OR t.receiverCard IN :cardIds")
    List<TransferHistory> findBySenderOrReceiverCardIds(@Param("cardIds") List<Card> cardIds);

    @Query("SELECT t FROM TransferHistory t WHERE t.senderCard.user.id = :userId OR t.receiverCard.user.id = :userId")
    List<TransferHistory> findAllByUserId(@Param("userId") Long userId);
}

package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing a record of a transfer between cards.
 */
@Data
@AllArgsConstructor
public class TransferHistoryResponse {

    /**
     * Unique identifier of the transfer record.
     */
    private Long id;

    /**
     * ID of the sender's card.
     */
    private Long senderCardId;

    /**
     * ID of the receiver's card.
     */
    private Long receiverCardId;

    /**
     * Amount of money transferred.
     */
    private BigDecimal amount;

    /**
     * Date and time when the transfer occurred.
     */
    private LocalDateTime transferredAt;
}

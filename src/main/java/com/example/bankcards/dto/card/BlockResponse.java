package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * Represents a response containing information about a card block request.
 */
@Data
@AllArgsConstructor
public class BlockResponse {

    /**
     * Unique identifier of the block request.
     */
    private Long id;

    /**
     * Identifier of the user who requested the block.
     */
    private Long userId;

    /**
     * Identifier of the card to be blocked.
     */
    private Long cardId;

    /**
     * Date and time when the block request was submitted.
     */
    private LocalDateTime requestedAt;
}

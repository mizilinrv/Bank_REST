package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for changing the status of a card.
 */
@Data
@AllArgsConstructor
public class ChangeStatusRequest {

    /**
     * The new status to be applied to the card.
     */
    @NotNull
    private CardStatus status;
}


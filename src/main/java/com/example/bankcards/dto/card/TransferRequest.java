package com.example.bankcards.dto.card;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO representing a request to transfer funds between two cards.
 */
@Data
@AllArgsConstructor
public class TransferRequest {

    /**
     * ID of the card from which the funds will be withdrawn.
     */
    @NotNull
    private Long fromCardId;

    /**
     * ID of the card to which the funds will be transferred.
     */
    @NotNull
    private Long toCardId;

    /**
     * Amount to be transferred. Must be greater than 0.
     */
    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}


package com.example.bankcards.dto.card;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new card.
 */
@Data
@AllArgsConstructor
public class CreateCardRequest {

    /**
     * ID of the user to whom the card will be assigned.
     */
    @NotNull
    private Long userId;

    /**
     * Initial balance of the card.
     */
    private BigDecimal balance;

    /**
     * Expiration date of the card. Must be a future date.
     */
    @NotNull
    @Future
    private LocalDate expirationDate;
}

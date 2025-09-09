package com.example.bankcards.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a response containing information about a bank card.
 */
@Data
@AllArgsConstructor
public class CardResponse {

    /**
     * Unique identifier of the card.
     */
    private Long id;

    /**
     * Masked card number for security purposes.
     */
    @JsonProperty("card_number")
    private String maskedCardNumber;

    /**
     * Full name of the card owner.
     */
    @JsonProperty("owner")
    private String ownerFullName;

    /**
     * Expiration date of the card.
     */
    private LocalDate expirationDate;

    /**
     * Current status of the card (e.g., ACTIVE, BLOCKED).
     */
    private String status;

    /**
     * Current balance of the card.
     */
    private BigDecimal balance;
}

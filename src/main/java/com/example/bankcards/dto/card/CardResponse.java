package com.example.bankcards.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CardResponse {

    private Long id;

    @JsonProperty("card_number")
    private String maskedCardNumber;

    @JsonProperty("owner")
    private String ownerFullName;

    private LocalDate expirationDate;
    private String status;
    private BigDecimal balance;
}

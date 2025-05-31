package com.example.bankcards.dto.card;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateCardRequest {

    @NotNull
    private Long userId;

    private BigDecimal balance;

    @NotNull
    @Future
    private LocalDate expirationDate;
}

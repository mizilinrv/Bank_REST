package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransferHistoryResponse {
    Long id;
    Long senderCardId;
    Long receiverCardId;
    BigDecimal amount;
    LocalDateTime transferredAt;
}

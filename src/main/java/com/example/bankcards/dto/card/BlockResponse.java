package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class BlockResponse {
    Long id;
    Long userId;
    Long cardId;
    LocalDateTime requestedAt;
}

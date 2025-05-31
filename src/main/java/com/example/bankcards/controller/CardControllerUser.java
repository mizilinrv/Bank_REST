package com.example.bankcards.controller;

import com.example.bankcards.dto.card.TransferHistoryResponse;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.TransferRequest;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Tag(name = "Card User API", description = "Операции пользователя с картами")
@Slf4j
public class CardControllerUser {

    private final CardService cardService;
    private final BlockRequestService blockRequestService;

    @Operation(summary = "Получение списка своих карт")
    @ApiResponse(responseCode = "200", description = "Список карт пользователя",
            content = @Content(schema = @Schema(implementation = CardResponse.class)))
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getMyCards(
            @AuthenticationPrincipal final UserDetails user,
            @RequestParam final Optional<String> status,
            final Pageable pageable
    ) {
        log.info("Получение карт пользователя: {}", user.getUsername());
        return ResponseEntity.ok(cardService.getUserCards(user.getUsername(), status, pageable));
    }

    @Operation(summary = "Получение информации о карте по ID",
    responses = {
            @ApiResponse(responseCode = "200", description = "Информация о карте",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(
            @Parameter(description = "ID карты") @PathVariable final Long id,
            @AuthenticationPrincipal final UserDetails user
    ) {
        log.info("Запрос на получение карты ID={} от пользователя {}", id, user.getUsername());
        CardResponse card = cardService.getCardById(id, user.getUsername());
        return ResponseEntity.ok(card);
    }

    @Operation(summary = "Получение баланса по карте",
    responses = {
            @ApiResponse(responseCode = "200", description = "Баланс карты",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "ID карты") @PathVariable final Long id,
            @AuthenticationPrincipal final UserDetails user
    )  {
        log.info("Пользователь {} запрашивает баланс карты ID={}", user.getUsername(), id);
        return ResponseEntity.ok(cardService.getCardBalance(id, user.getUsername()));
    }

    @Operation(summary = "Отправка запроса на блокировку карты",
    responses = {
            @ApiResponse(responseCode = "200", description = "Запрос успешно отправлен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PostMapping("/{id}/block-request")
    public ResponseEntity<Void> requestBlock(
            @Parameter(description = "ID карты") @PathVariable final Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("Пользователь {} отправляет запрос на блокировку карты ID={}", user.getUsername(), id);
        blockRequestService.createRequest(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Перевод средств между картами",
    responses = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @Valid @RequestBody final TransferRequest request,
            @AuthenticationPrincipal final UserDetails user
    ) {
        log.info("Пользователь {} инициирует перевод: {}", user.getUsername(), request);
        cardService.transferBetweenCards(request, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "История переводов пользователя")
    @ApiResponse(responseCode = "200", description = "Список переводов",
            content = @Content(schema = @Schema(implementation = TransferHistoryResponse.class)))
    @GetMapping("/transfer/all")
    public ResponseEntity<List<TransferHistoryResponse>> getAllTransfer(
            @AuthenticationPrincipal final UserDetails user,
            final Pageable pageable
    ) {
        log.info("Запрос истории переводов пользователя {}", user.getUsername());
        return cardService.getAllTransferUser(user.getUsername(), pageable);
    }
}

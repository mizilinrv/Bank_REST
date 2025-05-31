package com.example.bankcards.controller;

import com.example.bankcards.dto.card.*;
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
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/cards/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Card Admin API", description = "Управление картами  для администраторов")
@Slf4j
public class CardControllerAdmin {

    private final CardService cardService;
    private final BlockRequestService blockRequestService;

    @Operation(summary = "Создание новой карты")
    @ApiResponse(responseCode = "200", description = "Карта успешно создана",
            content = @Content(schema = @Schema(implementation = CardResponse.class)))
    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @RequestBody @Valid final CreateCardRequest request) {
        log.info("Карта успешно создана");
        return ResponseEntity.ok(cardService.createCard(request));
    }

    @Operation(summary = "Изменение статуса карты",
    responses = {
            @ApiResponse(responseCode = "200", description = "Статус успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @Parameter(description = "ID карты") @PathVariable final Long id,
            @RequestBody @Valid final ChangeStatusRequest request) {
        cardService.changeStatus(id, request);
        log.info("Статус карты с ID {} успешно обновлен", id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление карты",
    responses = {
            @ApiResponse(responseCode = "204", description = "Карта успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID карты") @PathVariable final Long id) {
        cardService.delete(id);
        log.info("Карта с ID {} успешно удалена", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получение всех карт (постранично)")
    @ApiResponse(responseCode = "200", description = "Список карт",
            content = @Content(schema = @Schema(implementation = CardResponse.class)))
    @GetMapping("/all")
    public ResponseEntity<Page<CardResponse>> getAllCards(final Pageable pageable) {
        Page<CardResponse> page = cardService.getAllCards(pageable);
        log.info("Получено {} карт", page.getNumberOfElements());
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Получение всех заявок на блокировку")
    @ApiResponse(responseCode = "200", description = "Список заявок",
            content = @Content(schema = @Schema(implementation = BlockResponse.class)))
    @GetMapping
    public ResponseEntity<List<BlockResponse>> getAllPending() {
        List<BlockResponse> list = blockRequestService.getAllPendingRequests();
        log.info("Получено {} заявок на блокировку", list.size());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Получение всей истории переводов")
    @ApiResponse(responseCode = "200", description = "Список переводов",
            content = @Content(schema = @Schema(implementation = TransferHistoryResponse.class)))
    @GetMapping("/transfer")
    public ResponseEntity<List<TransferHistoryResponse>> getAllTransfer() {
        List<TransferHistoryResponse> list = cardService.getAllTransfer();
        log.info("Получено {} записей истории переводов", list.size());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Получение истории переводов по ID пользователя")
    @ApiResponse(responseCode = "200", description = "Список переводов",
            content = @Content(schema = @Schema(implementation = TransferHistoryResponse.class)))
    @GetMapping("/{id}/transfer")
    public ResponseEntity<List<TransferHistoryResponse>> getTransferByUserId(
            @Parameter(description = "ID пользователя") @PathVariable final Long id) {
        List<TransferHistoryResponse> list = cardService.getTransferByUserId(id);
        log.info("Получено {} записей истории переводов для пользователя {}", list.size(), id);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Обработка запроса на блокировку карты",
    responses = {
            @ApiResponse(responseCode = "200", description = "Заявка обработана"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена"),
            @ApiResponse(responseCode = "500", description = "Ошибка обработки заявки")
    })
    @PostMapping("/{id}/process")
    public ResponseEntity<Void> processBlockRequest(
            @Parameter(description = "ID заявки") @PathVariable final Long id) {
        blockRequestService.processBlockRequest(id);
        log.info("Заявка на блокировку с ID {} успешно обработана", id);
        return ResponseEntity.ok().build();
    }
}
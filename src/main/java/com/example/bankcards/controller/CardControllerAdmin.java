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

/**
 * REST controller for administrative operations on cards.
 * Provides endpoints for managing cards, block requests, and transfer history.
 * <p>
 * Only accessible to users with the ADMIN role.
 */
@RestController
@RequestMapping("/api/cards/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Card Admin API", description = "Administrative operations for managing cards, block requests, and transfers")
@Slf4j
public class CardControllerAdmin {

    private final CardService cardService;
    private final BlockRequestService blockRequestService;

    /**
     * Creates a new card.
     *
     * @param request DTO with card creation data
     * @return the created card response
     */
    @Operation(summary = "Create a new card")
    @ApiResponse(responseCode = "200", description = "Card successfully created",
            content = @Content(schema = @Schema(implementation = CardResponse.class)))
    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @RequestBody @Valid final CreateCardRequest request) {
        log.info("Card successfully created");
        return ResponseEntity.ok(cardService.createCard(request));
    }

    /**
     * Updates the status of a card.
     *
     * @param id      the card ID
     * @param request DTO with new status
     * @return 200 if status updated, 404 if card not found
     */
    @Operation(summary = "Update card status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            })
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @Parameter(description = "Card ID") @PathVariable final Long id,
            @RequestBody @Valid final ChangeStatusRequest request) {
        cardService.changeStatus(id, request);
        log.info("Card status with ID {} successfully updated", id);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a card by ID.
     *
     * @param id the card ID
     * @return 204 if deleted, 404 if not found
     */
    @Operation(summary = "Delete a card",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Card successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "Card ID") @PathVariable final Long id) {
        cardService.delete(id);
        log.info("Card with ID {} successfully deleted", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all cards with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of cards
     */
    @Operation(summary = "Get all cards (paginated)")
    @ApiResponse(responseCode = "200", description = "List of cards",
            content = @Content(schema = @Schema(implementation = CardResponse.class)))
    @GetMapping("/all")
    public ResponseEntity<Page<CardResponse>> getAllCards(final Pageable pageable) {
        Page<CardResponse> page = cardService.getAllCards(pageable);
        log.info("Retrieved {} cards", page.getNumberOfElements());
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves all pending block requests.
     *
     * @return list of block requests
     */
    @Operation(summary = "Get all pending block requests")
    @ApiResponse(responseCode = "200", description = "List of block requests",
            content = @Content(schema = @Schema(implementation = BlockResponse.class)))
    @GetMapping
    public ResponseEntity<List<BlockResponse>> getAllPending() {
        List<BlockResponse> list = blockRequestService.getAllPendingRequests();
        log.info("Retrieved {} block requests", list.size());
        return ResponseEntity.ok(list);
    }

    /**
     * Retrieves all transfer history.
     *
     * @return list of transfer history records
     */
    @Operation(summary = "Get all transfer history")
    @ApiResponse(responseCode = "200", description = "List of transfer history records",
            content = @Content(schema = @Schema(implementation = TransferHistoryResponse.class)))
    @GetMapping("/transfer")
    public ResponseEntity<List<TransferHistoryResponse>> getAllTransfer() {
        List<TransferHistoryResponse> list = cardService.getAllTransfer();
        log.info("Retrieved {} transfer history records", list.size());
        return ResponseEntity.ok(list);
    }

    /**
     * Retrieves transfer history by user ID.
     *
     * @param id the user ID
     * @return list of transfer history records for the user
     */
    @Operation(summary = "Get transfer history by user ID")
    @ApiResponse(responseCode = "200", description = "List of transfer history records",
            content = @Content(schema = @Schema(implementation = TransferHistoryResponse.class)))
    @GetMapping("/{id}/transfer")
    public ResponseEntity<List<TransferHistoryResponse>> getTransferByUserId(
            @Parameter(description = "User ID") @PathVariable final Long id) {
        List<TransferHistoryResponse> list = cardService.getTransferByUserId(id);
        log.info("Retrieved {} transfer history records for user {}", list.size(), id);
        return ResponseEntity.ok(list);
    }

    /**
     * Processes a block request by its ID.
     *
     * @param id the block request ID
     * @return 200 if processed, 404 if not found, 500 if error occurred
     */
    @Operation(summary = "Process block request",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Block request processed"),
                    @ApiResponse(responseCode = "404", description = "Block request not found"),
                    @ApiResponse(responseCode = "500", description = "Error while processing block request")
            })
    @PostMapping("/{id}/process")
    public ResponseEntity<Void> processBlockRequest(
            @Parameter(description = "Block request ID") @PathVariable final Long id) {
        blockRequestService.processBlockRequest(id);
        log.info("Block request with ID {} successfully processed", id);
        return ResponseEntity.ok().build();
    }
}

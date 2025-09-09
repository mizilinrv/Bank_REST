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

/**
 * REST controller for user operations on cards.
 * Provides endpoints for viewing user cards, requesting card blocking,
 * checking balances, performing transfers, and viewing transfer history.
 * <p>
 * Only accessible to users with the USER role.
 */
@RestController
@RequestMapping("/api/cards/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Tag(name = "Card User API", description = "User operations for managing personal cards and transfers")
@Slf4j
public class CardControllerUser {

    private final CardService cardService;
    private final BlockRequestService blockRequestService;

    /**
     * Retrieves the list of user's own cards, with optional filtering by status.
     *
     * @param user   the authenticated user
     * @param status optional card status filter
     * @param pageable pagination parameters
     * @return a page of user card responses
     */
    @Operation(summary = "Get user's own cards")
    @ApiResponse(responseCode = "200", description = "List of user's cards",
            content = @Content(schema = @Schema(implementation = CardResponse.class)))
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getMyCards(
            @AuthenticationPrincipal final UserDetails user,
            @RequestParam final Optional<String> status,
            final Pageable pageable
    ) {
        log.info("Retrieving cards for user: {}", user.getUsername());
        return ResponseEntity.ok(cardService.getUserCards(user.getUsername(), status, pageable));
    }

    /**
     * Retrieves card details by card ID.
     *
     * @param id   the card ID
     * @param user the authenticated user
     * @return the card details if accessible
     */
    @Operation(summary = "Get card details by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card details",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(
            @Parameter(description = "Card ID") @PathVariable final Long id,
            @AuthenticationPrincipal final UserDetails user
    ) {
        log.info("User {} requested card with ID={}", user.getUsername(), id);
        CardResponse card = cardService.getCardById(id, user.getUsername());
        return ResponseEntity.ok(card);
    }

    /**
     * Retrieves the balance of a specific card.
     *
     * @param id   the card ID
     * @param user the authenticated user
     * @return the card balance
     */
    @Operation(summary = "Get card balance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card balance",
                            content = @Content(schema = @Schema(implementation = BigDecimal.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "Card ID") @PathVariable final Long id,
            @AuthenticationPrincipal final UserDetails user
    ) {
        log.info("User {} requested balance for card ID={}", user.getUsername(), id);
        return ResponseEntity.ok(cardService.getCardBalance(id, user.getUsername()));
    }

    /**
     * Submits a block request for a specific card.
     *
     * @param id   the card ID
     * @param user the authenticated user
     * @return 200 if request successfully submitted
     */
    @Operation(summary = "Submit card block request",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Block request successfully submitted"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            })
    @PostMapping("/{id}/block-request")
    public ResponseEntity<Void> requestBlock(
            @Parameter(description = "Card ID") @PathVariable final Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("User {} submitted block request for card ID={}", user.getUsername(), id);
        blockRequestService.createRequest(id, user.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Transfers funds between user's cards.
     *
     * @param request transfer details
     * @param user    the authenticated user
     * @return 200 if transfer successful, 400 for invalid data, 403 for access denied
     */
    @Operation(summary = "Transfer funds between cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transfer completed"),
                    @ApiResponse(responseCode = "400", description = "Invalid transfer data"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @Valid @RequestBody final TransferRequest request,
            @AuthenticationPrincipal final UserDetails user
    ) {
        log.info("User {} initiated transfer: {}", user.getUsername(), request);
        cardService.transferBetweenCards(request, user.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the user's transfer history.
     *
     * @param user the authenticated user
     * @param pageable pagination parameters
     * @return list of transfer history records
     */
    @Operation(summary = "Get user's transfer history")
    @ApiResponse(responseCode = "200", description = "List of user's transfer history records",
            content = @Content(schema = @Schema(implementation = TransferHistoryResponse.class)))
    @GetMapping("/transfer/all")
    public ResponseEntity<List<TransferHistoryResponse>> getAllTransfer(
            @AuthenticationPrincipal final UserDetails user,
            final Pageable pageable
    ) {
        log.info("Retrieving transfer history for user {}", user.getUsername());
        return cardService.getAllTransferUser(user.getUsername(), pageable);
    }
}


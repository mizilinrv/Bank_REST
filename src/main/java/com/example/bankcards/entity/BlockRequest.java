package com.example.bankcards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Represents a request to block a card in the system.
 * <p>
 * Each request is associated with a {@link User} who submitted it
 * and a {@link Card} that is requested to be blocked.
 * The request contains metadata about its creation time and whether
 * it has been processed.
 * </p>
 */
@Entity
@Table(name = "block_requests")
@Getter
@Setter
@NoArgsConstructor
public class BlockRequest {

    /**
     * The unique identifier of the block request.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who submitted the block request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * The card associated with this block request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    /**
     * The timestamp when the block request was created.
     * Default value is the current time at instantiation.
     */
    private LocalDateTime requestedAt = LocalDateTime.now();

    /**
     * Indicates whether the block request has been processed.
     */
    private boolean processed = false;
}

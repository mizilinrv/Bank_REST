package com.example.bankcards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a record of a transfer between two cards.
 * <p>
 * Contains information about the sender and receiver cards,
 * the amount transferred,
 * and the timestamp of the transfer.
 * </p>
 */
@Entity
@Getter
@Setter
@Table(name = "transfer_history")
public class TransferHistory {

    /** Unique identifier of the transfer record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The card that sent the funds. */
    @ManyToOne
    @JoinColumn(name = "sender_card_id")
    private Card senderCard;

    /** The card that received the funds. */
    @ManyToOne
    @JoinColumn(name = "receiver_card_id")
    private Card receiverCard;

    /** Amount of money transferred. Cannot be null. */
    @Column(nullable = false)
    private BigDecimal amount;

    /** Timestamp when the transfer occurred. */
    @Column(name = "transferred_at")
    private LocalDateTime transferredAt;
}

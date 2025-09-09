package com.example.bankcards.entity;

/**
 * Represents the status of a bank card.
 * <p>
 * The status indicates whether a card is currently active, blocked, or expired.
 * </p>
 */
public enum CardStatus {

    /** The card is active and can be used for transactions. */
    ACTIVE,

    /** The card is blocked and cannot be used for transactions. */
    BLOCKED,

    /** The card has expired and is no longer valid. */
    EXPIRED
}

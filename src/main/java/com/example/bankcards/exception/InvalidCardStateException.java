package com.example.bankcards.exception;

/**
 * Exception thrown when a card is in an invalid state
 * for the requested operation.
 * <p>
 * This exception indicates that the operation cannot be performed because
 * the card's current status or state does not allow it.
 * </p>
 */
public class InvalidCardStateException extends RuntimeException {

    /**
     * Constructs a new InvalidCardStateException with
     * the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCardStateException(final String message) {
        super(message);
    }
}

package com.example.bankcards.exception;

/**
 * Exception thrown when an attempt is made to change a card's status
 * in an invalid or disallowed way.
 * <p>
 * This exception indicates that the requested status transition
 * for the card is not permitted.
 * </p>
 */
public class InvalidCardStatusChangeException extends RuntimeException {

    /**
     * Constructs a new InvalidCardStatusChangeException with
     * the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCardStatusChangeException(final String message) {
        super(message);
    }
}

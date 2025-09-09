package com.example.bankcards.exception;

/**
 * Exception thrown when a card has an invalid or unexpected status
 * that prevents the requested operation from being performed.
 * <p>
 * This exception indicates that the card's current status does not
 * allow the intended action.
 * </p>
 */
public class InvalidCardStatusException extends RuntimeException {

    /**
     * Constructs a new InvalidCardStatusException with
     * the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCardStatusException(final String message) {
        super(message);
    }
}

package com.example.bankcards.exception;

/**
 * Exception thrown when a card is not found.
 * <p>
 * This runtime exception is typically used in the service or controller layers
 * to indicate that an attempt to access or modify a card failed
 * because the card does not exist in the database.
 * </p>
 */
public class CardNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code CardNotFoundException}
     * with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public CardNotFoundException(final String message) {
        super(message);
    }
}

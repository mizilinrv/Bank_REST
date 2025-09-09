package com.example.bankcards.exception;

/**
 * Exception thrown when an administrator fails to create a new card.
 * <p>
 * This is a runtime exception, typically used in service or controller
 * layers to indicate that the card creation process could not be completed.
 * </p>
 */
public class AdminCardCreationException extends RuntimeException {

    /**
     * Constructs a new {@code AdminCardCreationException}
     * with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public AdminCardCreationException(final String message) {
        super(message);
    }
}

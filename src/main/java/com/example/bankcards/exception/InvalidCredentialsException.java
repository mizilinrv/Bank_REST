package com.example.bankcards.exception;

/**
 * Exception thrown when user authentication fails due to invalid credentials.
 * <p>
 * This exception indicates that
 * the provided login information (e.g., username or password)
 * is incorrect and authentication cannot be completed.
 * </p>
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructs a new InvalidCredentialsException with
     * the specified detail message.
     *
     * @param message the detail message explaining why
     *                the credentials are invalid
     */
    public InvalidCredentialsException(final String message) {
        super(message);
    }
}

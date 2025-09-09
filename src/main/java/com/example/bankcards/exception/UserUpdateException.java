package com.example.bankcards.exception;

/**
 * Exception thrown when an error occurs while updating a user.
 * <p>
 * This exception can wrap another underlying exception that caused
 * the user update operation to fail.
 * </p>
 */
public class UserUpdateException extends RuntimeException {

    /**
     * Constructs a new UserUpdateException with
     * the specified detail message and cause.
     *
     * @param message the detail message describing the error
     * @param cause   the underlying exception that caused this error
     */
    public UserUpdateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}


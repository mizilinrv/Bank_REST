package com.example.bankcards.exception;

/**
 * Exception thrown when a user attempts an operation that is not permitted.
 * <p>
 * This runtime exception is typically used to indicate that the current user
 * does not have the required permissions or
 * role to perform the requested action.
 * </p>
 */
public class ForbiddenOperationException extends RuntimeException {

    /**
     * Constructs a new {@code ForbiddenOperationException}
     * with the specified detail message.
     *
     * @param message the detail message explaining
     *               why the operation is forbidden
     */
    public ForbiddenOperationException(final String message) {
        super(message);
    }
}

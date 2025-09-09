package com.example.bankcards.exception;

/**
 * Exception thrown when a block request is not found.
 * <p>
 * This runtime exception is typically used in the service or controller layers
 * to indicate that an attempt to access or process a block request failed
 * because it does not exist in the database.
 * </p>
 */
public class BlockRequestNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code BlockRequestNotFoundException}
     * with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public BlockRequestNotFoundException(final String message) {
        super(message);
    }
}

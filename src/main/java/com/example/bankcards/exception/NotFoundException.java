package com.example.bankcards.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * <p>
 * This exception is used to indicate that an entity, record, or object
 * requested by the user or system does not exist in the database or storage.
 * </p>
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message describing the missing resource
     */
    public NotFoundException(final String message) {
        super(message);
    }
}

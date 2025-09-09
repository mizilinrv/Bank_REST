package com.example.bankcards.exception;

/**
 * Exception thrown when an encryption or decryption operation fails.
 * <p>
 * This runtime exception is typically used in services that handle
 * sensitive data encryption, such as card numbers or passwords,
 * to indicate that an unexpected error occurred during the process.
 * </p>
 */
public class EncryptionException extends RuntimeException {

    /**
     * Constructs a new {@code EncryptionException}
     * with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the original throwable that caused this exception
     */
    public EncryptionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

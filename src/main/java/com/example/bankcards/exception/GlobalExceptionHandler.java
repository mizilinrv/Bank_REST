package com.example.bankcards.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the REST API.
 * <p>
 * This class handles all application-specific and generic exceptions thrown
 * by controllers and maps them to appropriate HTTP responses with status codes
 * and messages.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link NotFoundException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 404 Not Found with the exception message
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(final NotFoundException ex) {
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles {@link ForbiddenOperationException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 403 Forbidden with the exception message
     */
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<String> handleForbidden(
            final ForbiddenOperationException ex) {
        return ResponseEntity.status(
                HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    /**
     * Handles all other generic {@link Exception} exceptions.
     *
     * @param ex the exception
     * @return HTTP 500 Internal Server Error with the exception message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(final Exception ex) {
        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /**
     * Handles {@link InvalidCardStateException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 400 Bad Request with the exception message
     */
    @ExceptionHandler(InvalidCardStateException.class)
    public ResponseEntity<String> handleInvalidCardState(
            final InvalidCardStateException ex) {
        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles {@link UserUpdateException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 500 Internal Server Error with the exception message
     */
    @ExceptionHandler(UserUpdateException.class)
    public ResponseEntity<String> handleUserUpdateException(
            final UserUpdateException ex) {
        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /**
     * Handles {@link InvalidCredentialsException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 401 Unauthorized with the exception message
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentials(
            final InvalidCredentialsException ex) {
        return ResponseEntity.status(
                HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    /**
     * Handles {@link BlockRequestNotFoundException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 404 Not Found with the exception message
     */
    @ExceptionHandler(BlockRequestNotFoundException.class)
    public ResponseEntity<String> handleBlockRequestNotFound(
            final BlockRequestNotFoundException ex) {
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles {@link AdminCardCreationException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 400 Bad Request with the exception message
     */
    @ExceptionHandler(AdminCardCreationException.class)
    public ResponseEntity<String> handleAdminCardCreation(
            final AdminCardCreationException ex) {
        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles {@link InvalidCardStatusChangeException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 400 Bad Request with the exception message
     */
    @ExceptionHandler(InvalidCardStatusChangeException.class)
    public ResponseEntity<String> handleInvalidCardStatusChange(
            final InvalidCardStatusChangeException ex) {
        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles {@link CardNotFoundException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 404 Not Found with the exception message
     */
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFound(
            final CardNotFoundException ex) {
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles {@link InvalidCardStatusException} exceptions.
     *
     * @param ex the exception
     * @return HTTP 400 Bad Request with the exception message
     */
    @ExceptionHandler(InvalidCardStatusException.class)
    public ResponseEntity<String> handleInvalidCardStatusException(
            final InvalidCardStatusException ex) {
        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}



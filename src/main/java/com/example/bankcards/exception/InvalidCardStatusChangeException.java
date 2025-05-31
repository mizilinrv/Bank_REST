package com.example.bankcards.exception;

public class InvalidCardStatusChangeException extends RuntimeException {
    public InvalidCardStatusChangeException(String message) {
        super(message);
    }
}
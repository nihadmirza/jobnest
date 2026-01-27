package com.example.jobnest.exception;

/**
 * Thrown when a password reset token is missing, invalid, or expired.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}


package com.example.jobnest.exception;

/**
 * Used when we need to show the payment cancellation page with a friendly message.
 */
public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(String message) {
        super(message);
    }
}


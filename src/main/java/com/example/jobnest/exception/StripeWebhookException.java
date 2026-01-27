package com.example.jobnest.exception;

/**
 * Used to return a non-2xx response to Stripe webhooks on signature/processing errors.
 */
public class StripeWebhookException extends RuntimeException {
    public StripeWebhookException(String message, Throwable cause) {
        super(message, cause);
    }
}


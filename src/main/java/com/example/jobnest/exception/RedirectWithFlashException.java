package com.example.jobnest.exception;

/**
 * Exception used to redirect back to a safe page while preserving a flash message.
 * Useful for MVC flows where we don't want to render a generic error page.
 */
public class RedirectWithFlashException extends RuntimeException {
    private final String redirectTo;
    private final String flashKey;
    private final String flashMessage;

    public RedirectWithFlashException(String redirectTo, String flashKey, String flashMessage) {
        super(flashMessage);
        this.redirectTo = redirectTo;
        this.flashKey = flashKey;
        this.flashMessage = flashMessage;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public String getFlashKey() {
        return flashKey;
    }

    public String getFlashMessage() {
        return flashMessage;
    }
}


package com.example.jobnest.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s with id %s not found", resourceType, resourceId), "RESOURCE_NOT_FOUND");
    }
}

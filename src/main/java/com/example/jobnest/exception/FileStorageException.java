package com.example.jobnest.exception;

/**
 * Exception thrown when file operations fail.
 */
public class FileStorageException extends BusinessException {

    public FileStorageException(String message) {
        super(message, "FILE_STORAGE_ERROR");
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, "FILE_STORAGE_ERROR", cause);
    }
}

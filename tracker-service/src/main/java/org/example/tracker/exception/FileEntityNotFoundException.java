package org.example.tracker.exception;

public class FileEntityNotFoundException extends RuntimeException {
    public FileEntityNotFoundException() {
    }

    public FileEntityNotFoundException(String message) {
        super(message);
    }
}

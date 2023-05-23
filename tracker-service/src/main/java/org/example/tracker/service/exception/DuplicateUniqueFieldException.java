package org.example.tracker.service.exception;

public class DuplicateUniqueFieldException extends RuntimeException {
    public DuplicateUniqueFieldException() {
    }

    public DuplicateUniqueFieldException(String message) {
        super(message);
    }
}

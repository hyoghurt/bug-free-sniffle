package org.example.tracker.exception;

public class DuplicateUniqueFieldException extends RuntimeException {
    public DuplicateUniqueFieldException() {
    }

    public DuplicateUniqueFieldException(String message) {
        super(message);
    }
}

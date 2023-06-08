package org.example.tracker.exception;

public class RequiredFieldException extends RuntimeException {
    public RequiredFieldException() {
    }

    public RequiredFieldException(String message) {
        super(message);
    }
}

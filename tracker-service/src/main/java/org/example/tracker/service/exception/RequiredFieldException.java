package org.example.tracker.service.exception;

public class RequiredFieldException extends RuntimeException {
    public RequiredFieldException() {
    }

    public RequiredFieldException(String message) {
        super(message);
    }
}

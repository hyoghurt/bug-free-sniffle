package org.example.tracker.service.exception;

public class EmployeeAlreadyDeletedException extends RuntimeException {
    public EmployeeAlreadyDeletedException() {
    }

    public EmployeeAlreadyDeletedException(String message) {
        super(message);
    }
}

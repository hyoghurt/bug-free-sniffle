package org.example.tracker.exception;

public class EmployeeAlreadyDeletedException extends RuntimeException {
    public EmployeeAlreadyDeletedException() {
    }

    public EmployeeAlreadyDeletedException(String message) {
        super(message);
    }
}

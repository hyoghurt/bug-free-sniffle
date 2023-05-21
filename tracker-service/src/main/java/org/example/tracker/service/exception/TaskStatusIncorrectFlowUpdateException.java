package org.example.tracker.service.exception;

public class TaskStatusIncorrectFlowUpdateException extends RuntimeException {
    public TaskStatusIncorrectFlowUpdateException(String message) {
        super(message);
    }
}

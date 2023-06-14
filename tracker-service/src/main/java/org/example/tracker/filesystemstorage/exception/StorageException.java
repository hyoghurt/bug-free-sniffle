package org.example.tracker.filesystemstorage.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}

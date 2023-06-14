package org.example.tracker.filesystemstorage.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StorageFileNotFoundException extends RuntimeException {
    public StorageFileNotFoundException(String message) {
        super(message);
    }
}

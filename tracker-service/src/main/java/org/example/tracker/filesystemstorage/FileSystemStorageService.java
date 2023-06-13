package org.example.tracker.filesystemstorage;

import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.UUID;

public interface FileSystemStorageService {

    UUID save(InputStream inputStream);

    Resource loadAsResource(String filename);
}

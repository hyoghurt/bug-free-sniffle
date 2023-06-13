package org.example.tracker.filesystemstorage;

import lombok.extern.slf4j.Slf4j;
import org.example.tracker.filesystemstorage.exception.StorageException;
import org.example.tracker.filesystemstorage.exception.StorageFileNotFoundException;
import org.example.tracker.filesystemstorage.property.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileSystemStorageServiceImpl implements FileSystemStorageService {
    private final Path rootLocation;

    @Autowired
    public FileSystemStorageServiceImpl(StorageProperties properties) throws IOException {
        this.rootLocation = Paths.get(properties.getLocation());
        createRootDirectory(this.rootLocation);
    }

    private void createRootDirectory(Path rootLocation) throws IOException {
        if (Files.notExists(rootLocation)) {
            Files.createDirectory(rootLocation);
            log.info("root directory created: {}", rootLocation);
        } else {
            log.info("root directory already exists: {}", rootLocation);
        }
    }

    @Override
    public UUID save(InputStream inputStream) {
        try {
            UUID uuid = UUID.randomUUID();
            Path destinationFile = rootLocation.resolve(uuid.toString()).normalize().toAbsolutePath();
            Files.copy(inputStream, destinationFile);
            return uuid;
        } catch (IOException e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new StorageException("A file of that name already exists.");
            }
            throw new StorageException("Failed to store file.");
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename);
        }
    }
}

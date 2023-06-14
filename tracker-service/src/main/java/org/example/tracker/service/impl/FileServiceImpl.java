package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.entity.FileEntity;
import org.example.tracker.exception.FileEntityNotFoundException;
import org.example.tracker.filesystemstorage.FileSystemStorageService;
import org.example.tracker.filesystemstorage.exception.StorageException;
import org.example.tracker.repository.FileRepository;
import org.example.tracker.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileSystemStorageService fileSystemStorageService;
    private Function<UUID, String> idToUrlFunction;

    @Override
    public void setIdToUrlFunction(Function<UUID, String> idToUrlFunction) {
        this.idToUrlFunction = idToUrlFunction;
    }

    @Override
    public UUID save(MultipartFile file) {
        checkEmptyFile(file);
        UUID id = saveFileInSystemStorage(file);
        FileEntity entity = FileEntity.builder()
                .id(id)
                .fileName(file.getOriginalFilename())
                .mime(file.getContentType())
                .build();
        fileRepository.save(entity);
        return id;
    }

    private void checkEmptyFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("empty file");
        }
    }

    private UUID saveFileInSystemStorage(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return fileSystemStorageService.save(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed get input stream from file");
        }
    }

    @Override
    public ResponseEntity<Resource> get(UUID id) {
        Resource resource = fileSystemStorageService.loadAsResource(id.toString());
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new FileEntityNotFoundException(id.toString()));
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(entity.getMime()))
                .body(resource);
    }

    @Override
    public String toUrl(UUID id) {
        return idToUrlFunction.apply(id);
    }
}

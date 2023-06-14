package org.example.tracker.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.function.Function;

public interface FileService {
    UUID save(MultipartFile file);
    ResponseEntity<Resource> get(UUID id);
    void setIdToUrlFunction(Function<UUID, String> idToUrlFunction);
    String toUrl(UUID id);
}

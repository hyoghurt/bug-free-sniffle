package org.example.tracker.repository;

import org.example.tracker.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID>{
}

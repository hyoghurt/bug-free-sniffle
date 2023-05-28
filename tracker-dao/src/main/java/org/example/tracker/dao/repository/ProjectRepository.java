package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer>,
        JpaSpecificationExecutor<ProjectEntity> {
}
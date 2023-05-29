package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<TaskEntity, Integer>,
        JpaSpecificationExecutor<TaskEntity> {
}
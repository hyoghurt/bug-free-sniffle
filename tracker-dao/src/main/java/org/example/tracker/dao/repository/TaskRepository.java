package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Integer>, TaskFilterRepository {
}
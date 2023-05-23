package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dto.task.TaskFilterParam;

import java.util.List;

public interface TaskFilterRepository {
    List<TaskEntity> findByFilter(TaskFilterParam filter);
}

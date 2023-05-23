package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dto.task.TaskFilterParam;

import java.util.List;

public interface TaskRepositoryCustom {
    List<TaskEntity> findByFilter(TaskFilterParam filter);
}

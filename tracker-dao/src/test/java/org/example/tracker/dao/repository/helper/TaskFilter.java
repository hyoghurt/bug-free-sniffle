package org.example.tracker.dao.repository.helper;

import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskStatus;

import java.time.Instant;
import java.util.List;

public class TaskFilter {
    public static boolean filter(TaskEntity e, TaskFilterParam param) {
        String query = param.getQuery();
        Integer authorId = param.getAuthorId();
        Integer assigneesId = param.getAssigneesId();
        Instant minCreatedDatetime = param.getMinCreatedDatetime();
        Instant maxCreatedDatetime = param.getMaxCreatedDatetime();
        Instant minDeadlineDatetime = param.getMinDeadlineDatetime();
        Instant maxDeadlineDatetime = param.getMaxDeadlineDatetime();
        List<TaskStatus> statuses = param.getStatuses();

        return (
                query == null
                        || e.getTitle().toUpperCase().contains(query.toUpperCase())
        ) && (
                statuses == null
                        || statuses.stream().anyMatch(s -> e.getStatus().equals(s))
        ) && (
                authorId == null
                        || e.getAuthorId().equals(authorId)
        ) && (
                assigneesId == null
                        || e.getAssignees().getId().equals(assigneesId)
        ) && (
                minCreatedDatetime == null
                        || e.getCreatedDatetime().isAfter(minCreatedDatetime)
        ) && (
                maxCreatedDatetime == null
                        || e.getCreatedDatetime().isBefore(maxCreatedDatetime)
        ) && (
                minDeadlineDatetime == null
                        || e.getDeadlineDatetime().isAfter(minDeadlineDatetime)
        ) && (
                maxDeadlineDatetime == null
                        || e.getDeadlineDatetime().isBefore(maxDeadlineDatetime)
        );
    }
}

package org.example.tracker;

import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskStatus;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class TaskFilter {
    public static List<TaskEntity> filter(List<TaskEntity> entities, TaskFilterParam param) {
        String query = param.getQuery();
        Integer authorId = param.getAuthorId();
        Integer assigneesId = param.getAssigneesId();
        Instant minCreatedDatetime = param.getMinCreatedDatetime();
        Instant maxCreatedDatetime = param.getMaxCreatedDatetime();
        Instant minDeadlineDatetime = param.getMinDeadlineDatetime();
        Instant maxDeadlineDatetime = param.getMaxDeadlineDatetime();
        List<TaskStatus> statuses = param.getStatuses();

        return entities.stream().filter(e ->
                        (
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
                        )
                )
                .sorted(Comparator.comparing(TaskEntity::getCreatedDatetime))
                .toList();
    }
}

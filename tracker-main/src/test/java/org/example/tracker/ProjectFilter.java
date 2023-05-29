package org.example.tracker;

import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dto.project.ProjectStatus;

import java.util.List;

public class ProjectFilter {
    public static List<ProjectEntity> filter(List<ProjectEntity> entities,
                                             String query, List<ProjectStatus> statuses) {

        return entities.stream().filter(e ->
                (
                        query == null
                                || e.getCode().toUpperCase().contains(query.toUpperCase())
                                || e.getName().toUpperCase().contains(query.toUpperCase())
                ) && (
                        statuses == null
                                || statuses.stream().anyMatch(s -> e.getStatus().equals(s))
                )
        ).toList();
    }
}

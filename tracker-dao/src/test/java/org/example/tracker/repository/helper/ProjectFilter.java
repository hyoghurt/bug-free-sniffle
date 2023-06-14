package org.example.tracker.repository.helper;

import org.example.tracker.entity.ProjectEntity;
import org.example.tracker.dto.project.ProjectFilterParam;

public class ProjectFilter {
    public static boolean filter(ProjectEntity e, ProjectFilterParam param) {
        return (
                param.getQuery() == null
                        || e.getCode().toUpperCase().contains(param.getQuery().toUpperCase())
                        || e.getName().toUpperCase().contains(param.getQuery().toUpperCase())
        ) && (
                param.getStatuses() == null
                        || param.getStatuses().stream().anyMatch(s -> e.getStatus().equals(s))
        );
    }
}

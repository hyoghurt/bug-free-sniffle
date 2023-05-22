package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dto.project.ProjectFilterParam;

import java.util.List;

public interface ProjectFilterRepository {
    List<ProjectEntity> findByFilter(ProjectFilterParam filter);
}

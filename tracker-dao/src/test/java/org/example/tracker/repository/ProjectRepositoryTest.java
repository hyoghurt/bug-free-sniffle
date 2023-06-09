package org.example.tracker.repository;

import org.example.tracker.Base;
import org.example.tracker.entity.ProjectEntity;
import org.example.tracker.repository.helper.ProjectFilter;
import org.example.tracker.repository.specification.ProfileSpecs;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectRepositoryTest extends Base {

    @BeforeEach
    void setUp() {
        projectEntities = initProjectEntities();
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
    }

    @Test
    void projectFilter() {
        equalsFilter(null, null);
        equalsFilter("test", null);
        equalsFilter(null, List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED));
        equalsFilter("test", List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED));
    }

    void equalsFilter(final String query, final List<ProjectStatus> statuses) {
        ProjectFilterParam param = ProjectFilterParam.builder()
                .query(query)
                .statuses(statuses)
                .build();

        List<ProjectEntity> expected = projectEntities.stream()
                .filter(e -> ProjectFilter.filter(e, param))
                .toList();

        List<ProjectEntity> actual = projectRepository
                .findAll(ProfileSpecs.byFilterParam(param));

        assertEquals(expected, actual);
    }
}
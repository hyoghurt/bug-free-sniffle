package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.helper.TaskFilter;
import org.example.tracker.dao.repository.specification.TaskSpecs;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskRepositoryTest extends Base {

    @Autowired
    TaskRepository taskRepository;

    List<TaskEntity> taskEntities;

    Instant current = Instant.now();

    @BeforeEach
    void setUp() {
        employeeEntities = initEmployeeEntities();
        projectEntities = initProjectEntities();
        taskEntities = initTaskEntities();
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void taskFilter() {
        TaskFilterParam param;

        param = TaskFilterParam.builder()
                .build();
        equalsFilter(param);

        param = TaskFilterParam.builder()
                .query("test")
                .build();
        equalsFilter(param);

        param = TaskFilterParam.builder()
                .query("test")
                .minCreatedDatetime(current.plus(4, ChronoUnit.HOURS))
                .maxDeadlineDatetime(current.plus(21, ChronoUnit.HOURS))
                .build();
        equalsFilter(param);

        param = TaskFilterParam.builder()
                .query("test")
                .statuses(List.of(TaskStatus.OPEN, TaskStatus.DONE))
                .minCreatedDatetime(current.plus(4, ChronoUnit.HOURS))
                .maxDeadlineDatetime(current.plus(21, ChronoUnit.HOURS))
                .build();
        equalsFilter(param);

        param = TaskFilterParam.builder()
                .query("test")
                .statuses(List.of(TaskStatus.OPEN, TaskStatus.DONE))
                .assigneesId(employeeEntities.get(0).getId())
                .authorId(employeeEntities.get(3).getId())
                .minCreatedDatetime(current.plus(6, ChronoUnit.HOURS))
                .maxCreatedDatetime(current.plus(40, ChronoUnit.HOURS))
                .minDeadlineDatetime(current.plus(4, ChronoUnit.HOURS))
                .maxDeadlineDatetime(current.plus(23, ChronoUnit.HOURS))
                .build();
        equalsFilter(param);
    }

    void equalsFilter(final TaskFilterParam param) {
        List<TaskEntity> expected = taskEntities.stream()
                .filter(e -> TaskFilter.filter(e, param))
                .sorted(Comparator.comparing(TaskEntity::getCreatedDatetime))
                .toList();

        List<TaskEntity> actual = taskRepository
                .findAll(TaskSpecs.byFilterParam(param));

        List<Integer> expectedIds = expected.stream().map(TaskEntity::getId).toList();
        List<Integer> actualIds = actual.stream().map(TaskEntity::getId).toList();

        assertEquals(expectedIds.size(), actualIds.size());
        assertEquals(expected, actual);
    }

    List<TaskEntity> initTaskEntities() {
        List<TaskEntity> entities = new ArrayList<>();
        TaskStatus[] taskStatuses = TaskStatus.values();

        int i = 0;

        for (EmployeeEntity author : employeeEntities) {
            for (ProjectEntity project : projectEntities) {
                TaskEntity taskEntity = genRandomTaskEntity(
                        author.getId(), null, project, current.plus(i, ChronoUnit.HOURS));
                taskEntity.setStatus(randomStatus(taskStatuses));
                entities.add(taskRepository.save(taskEntity));
                ++i;
            }
        }

        for (EmployeeEntity author : employeeEntities) {
            for (EmployeeEntity assignees : employeeEntities) {
                for (ProjectEntity project : projectEntities) {
                    TaskEntity taskEntity = genRandomTaskEntity(
                            author.getId(), assignees, project, current.plus(i, ChronoUnit.HOURS));
                    taskEntity.setStatus(randomStatus(taskStatuses));
                    entities.add(taskRepository.save(taskEntity));
                    ++i;
                }
            }
        }

        return entities;
    }

    int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    TaskStatus randomStatus(TaskStatus[] statuses) {
        return statuses[getRandomNumber(0, statuses.length)];
    }
}
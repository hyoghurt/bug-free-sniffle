package org.example.tracker;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class TaskFilterIntegrationTest extends BaseIntegrationTest {
    final String URL = "/v1/tasks";
    List<TaskEntity> taskEntities = new ArrayList<>();
    List<EmployeeEntity> employeeEntities = new ArrayList<>();
    List<ProjectEntity> projectEntities = new ArrayList<>();
    Instant current;

    int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    TaskStatus randomStatus(TaskStatus[] statuses) {
        return statuses[getRandomNumber(0, statuses.length)];
    }

    void initTaskEntities() {
        employeeEntities = initEmployeeEntities();
        projectEntities = initProjectEntities();
        taskEntities = new ArrayList<>();
        current = Instant.now();
        TaskStatus[] taskStatuses = TaskStatus.values();

        int i = 0;

        for (EmployeeEntity author : employeeEntities) {
            for (ProjectEntity project : projectEntities) {
                TaskEntity taskEntity = genRandomTaskEntity(
                        author.getId(), null, project, current.plus(i, ChronoUnit.HOURS));
                taskEntity.setStatus(randomStatus(taskStatuses));
                taskEntities.add(taskRepository.save(taskEntity));
                ++i;
            }
        }

        for (EmployeeEntity author : employeeEntities) {
            for (EmployeeEntity assignees : employeeEntities) {
                for (ProjectEntity project : projectEntities) {
                    TaskEntity taskEntity = genRandomTaskEntity(
                            author.getId(), assignees, project, current.plus(i, ChronoUnit.HOURS));
                    taskEntity.setStatus(randomStatus(taskStatuses));
                    taskEntities.add(taskRepository.save(taskEntity));
                    ++i;
                }
            }
        }
    }

    void getAllByParam(final TaskFilterParam param) throws Exception {

        List<TaskResp> expected = TaskFilter.filter(taskEntities, param).stream()
                .map(modelMapper::toTaskResp)
                .toList();

        MockHttpServletRequestBuilder requestBuilder = get(URL);

        String query = param.getQuery();
        if (query != null) requestBuilder.param("query", query);
        Integer authorId = param.getAuthorId();
        if (authorId != null) requestBuilder.param("authorId", String.valueOf(authorId));
        Integer assigneesId = param.getAssigneesId();
        if (assigneesId != null) requestBuilder.param("assigneesId", String.valueOf(assigneesId));
        Instant minCreatedDatetime = param.getMinCreatedDatetime();
        if (minCreatedDatetime != null) requestBuilder.param("minCreatedDatetime", String.valueOf(minCreatedDatetime));
        Instant maxCreatedDatetime = param.getMaxCreatedDatetime();
        if (maxCreatedDatetime != null) requestBuilder.param("maxCreatedDatetime", String.valueOf(maxCreatedDatetime));
        Instant minDeadlineDatetime = param.getMinDeadlineDatetime();
        if (minDeadlineDatetime != null)
            requestBuilder.param("minDeadlineDatetime", String.valueOf(minDeadlineDatetime));
        Instant maxDeadlineDatetime = param.getMaxDeadlineDatetime();
        if (maxDeadlineDatetime != null)
            requestBuilder.param("maxDeadlineDatetime", String.valueOf(maxDeadlineDatetime));

        List<TaskStatus> statuses = param.getStatuses();
        String[] array = (statuses != null) ? statuses.stream()
                .map(TaskStatus::name)
                .toArray(String[]::new) : null;
        if (statuses != null) requestBuilder.param("statuses", array);

        String content = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<TaskResp> actual = mapper.readValue(content, new TypeReference<>() {
        });

        List<Integer> expectedIds = expected.stream().map(TaskResp::getId).collect(Collectors.toList());
        List<Integer> actualIds = actual.stream().map(TaskResp::getId).collect(Collectors.toList());

        assertEquals(expectedIds.size(), actualIds.size());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    void getAllByParam_null_200() throws Exception {
        initTaskEntities();

        TaskFilterParam param = new TaskFilterParam();
        getAllByParam(param);

        param = TaskFilterParam.builder()
                .query("test")
                .build();
        getAllByParam(param);

        param = TaskFilterParam.builder()
                .query("test")
                .minCreatedDatetime(current.plus(4, ChronoUnit.HOURS))
                .maxDeadlineDatetime(current.plus(21, ChronoUnit.HOURS))
                .build();
        getAllByParam(param);

        param = TaskFilterParam.builder()
                .query("test")
                .statuses(List.of(TaskStatus.OPEN, TaskStatus.DONE))
                .minCreatedDatetime(current.plus(4, ChronoUnit.HOURS))
                .maxDeadlineDatetime(current.plus(21, ChronoUnit.HOURS))
                .build();
        getAllByParam(param);

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
        getAllByParam(param);
    }

    @Test
    void getAllByParam_200() throws Exception {
    }

    @Test
    void getAllByParam_incorrectType_400() throws Exception {
    }
}
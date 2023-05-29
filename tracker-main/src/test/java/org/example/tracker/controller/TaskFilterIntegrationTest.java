package org.example.tracker.controller;

import org.example.tracker.TaskFilter;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class TaskFilterIntegrationTest extends BaseIntegrationTest {
    final String URL = "/v1/tasks";
    List<TaskEntity> taskEntities = new ArrayList<>();
    List<EmployeeEntity> employeeEntities = new ArrayList<>();
    List<ProjectEntity> projectEntities = new ArrayList<>();

    void pararam() {
        employeeEntities = initEmployeeEntities();
        projectEntities = initProjectEntities();
        taskEntities = new ArrayList<>();

        Instant current = Instant.now();
        int i = 0;

        for(EmployeeEntity author: employeeEntities) {
            for(ProjectEntity project : projectEntities) {
                TaskEntity taskEntity = genRandomTaskEntity(
                        author.getId(), null, project, current.plus(i, ChronoUnit.HOURS));
                taskEntities.add(taskRepository.save(taskEntity));
                ++i;
            }
        }

        for(EmployeeEntity author: employeeEntities) {
            for(EmployeeEntity assignees: employeeEntities) {
                for (ProjectEntity project : projectEntities) {
                    TaskEntity taskEntity = genRandomTaskEntity(
                            author.getId(), assignees, project, current.plus(i, ChronoUnit.HOURS));
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
        if (minDeadlineDatetime != null) requestBuilder.param("minDeadlineDatetime", String.valueOf(minDeadlineDatetime));
        Instant maxDeadlineDatetime = param.getMaxDeadlineDatetime();
        if (maxDeadlineDatetime != null) requestBuilder.param("maxDeadlineDatetime", String.valueOf(maxDeadlineDatetime));

        List<TaskStatus> statuses = param.getStatuses();
        String[] array = (statuses != null) ? statuses.stream()
                .map(TaskStatus::name)
                .toArray(String[]::new) : null;
        if (statuses != null) requestBuilder.param("statuses", array);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    @Test
    void getAllByParam_null_200() throws Exception {
        TaskFilterParam param = new TaskFilterParam();
        getAllByParam(param);
    }

    @Test
    void getAllByParam_200() throws Exception {
    }

    @Test
    void getAllByParam_incorrectType_400() throws Exception {
    }
}
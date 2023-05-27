package org.example.tracker.controller;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskStatus;
import org.example.tracker.dto.team.EmployeeRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskIntegrationTest extends BaseIntegrationTest {
    final String URL = "/v1/task";
    final String UPN = "user@com.com";

    @Test
    void create_401() throws Exception {
        EmployeeEntity authorEntity = createEmployee(UPN);
        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_201() throws Exception {
        EmployeeEntity authorEntity = createEmployee(UPN);
        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);

        String content = mvc.perform(post(URL).with(user(UPN))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        TaskResp actual = mapper.readValue(content, TaskResp.class);
        assertEquals(TaskStatus.OPEN, actual.getStatus());
        assertEquals(authorEntity.getId(), actual.getAuthorId());
        assertNotNull(actual.getCreatedDatetime());
    }

    @Test
    void create_requiredField_400() throws Exception {
        TaskReq request = genRandomTaskReq(null, null);

        mvc.perform(post(URL).with(user(UPN))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void create_incorrectDeadline_400() throws Exception {
        ProjectEntity projectEntity = projectRepository.save(genRandomProjectEntity());

        TaskReq request = TaskReq.builder()
                .title(UUID.randomUUID().toString())
                .projectId(projectEntity.getId())
                .laborCostsInHours(9)
                .deadlineDatetime(Instant.now().plus(6, ChronoUnit.HOURS))
                .build();

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_assigneesNotInTeam_400() throws Exception {
    }

    @Test
    void create_assigneesIsDeleted_400() throws Exception {
    }

    @Test
    void update_201() throws Exception {
    }

    @Test
    void update_requiredField_400() throws Exception {
    }

    @Test
    void update_incorrectDeadline_400() throws Exception {
    }

    @Test
    void update_assigneesNotInTeam_400() throws Exception {
    }

    @Test
    void update_assigneesIsDeleted_400() throws Exception {
    }

    @Test
    void updateStatus_200() throws Exception {
    }

    @Test
    void updateStatus_incorrectFlow_400() throws Exception {
    }

    @Test
    void getAllByFilter_200() throws Exception {
    }

    EmployeeEntity createEmployee(String upn) {
        EmployeeEntity employeeEntity = employeeRepository.save(EmployeeEntity.builder()
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .upn(upn)
                .status(EmployeeStatus.ACTIVE)
                .build());
        return employeeRepository.save(employeeEntity);
    }
}
package org.example.tracker;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskStatus;
import org.example.tracker.dto.task.TaskUpdateStatusReq;
import org.example.tracker.dto.team.EmployeeRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskIntegrationTest extends Base {
    final String URL = "/v1/task";
    final String UPN = "user@com.com";

    // CREATE ____________________________________________
    ResultActions createResultActions(final TaskReq body) throws Exception {
        return mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    void create_401() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);

        createResultActions(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(UPN)
    void create_201() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);

        String content = createResultActions(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        TaskResp actual = mapper.readValue(content, TaskResp.class);
        assertEquals(TaskStatus.OPEN, actual.getStatus());
        assertEquals(authorEntity.getId(), actual.getAuthorId());
        assertNotNull(actual.getCreatedDatetime());
    }

    @Test
    @WithMockUser(UPN)
    void create_requiredField_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(null, null);

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void create_notValidDeadline_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);
        request.setLaborCostsInHours(9);
        request.setDeadlineDatetime(Instant.now().plus(6, ChronoUnit.HOURS));

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void create_assigneesNotInTeam_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), assigneesEntity.getId());

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void create_authorNotInTeam_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(assigneesEntity, EmployeeRole.TESTER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), assigneesEntity.getId());

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void create_assigneesIsDeleted_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        EmployeeEntity assigneesEntity = genRandomEmployeeEntity(EmployeeStatus.DELETED);
        employeeRepository.save(assigneesEntity);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.TESTER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), assigneesEntity.getId());

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void create_projectNotFound_404() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        TaskReq request = genRandomTaskReq(2, null);

        createResultActions(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(UPN)
    void create_assigneesNotFound_404() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set.of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), 23);

        createResultActions(request)
                .andExpect(status().isNotFound());
    }


    // UPDATE ____________________________________________
    ResultActions updateResultActions(final Integer id, final TaskReq body) throws Exception {
        return mvc.perform(put(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    @WithMockUser(UPN)
    void update_200() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);
        request.setTitle(UUID.randomUUID().toString());

        String content = updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TaskResp actual = mapper.readValue(content, TaskResp.class);
        assertEquals(request.getTitle(), actual.getTitle());
        assertNull(actual.getAssigneesId());
        assertNotNull(actual.getUpdateDatetime());
    }

    @Test
    @WithMockUser(UPN)
    void update_requiredField_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);
        request.setDeadlineDatetime(null);

        updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void update_notValidDeadline_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);
        request.setLaborCostsInHours(500);

        updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void update_assigneesNotInTeam_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), assigneesEntity.getId());

        updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void update_authorNotInTeam_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);

        updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void update_assigneesIsDeleted_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntity(EmployeeStatus.DELETED);
        employeeRepository.save(assigneesEntity);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), null, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), assigneesEntity.getId());

        updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void update_projectNotFound_404() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(3, null);

        updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(UPN)
    void update_assigneesNotFound_404() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), assigneesEntity.getId() + 1);

        updateResultActions(taskEntity.getId(), request)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(UPN)
    void update_taskNotFound_404() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity assigneesEntity = genRandomEmployeeEntityAndSave();

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER),
                        new TeamEmbeddable(assigneesEntity, EmployeeRole.ANALYST));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), assigneesEntity, projectEntity);
        taskRepository.save(taskEntity);

        TaskReq request = genRandomTaskReq(projectEntity.getId(), null);
        request.setTitle(UUID.randomUUID().toString());

        updateResultActions(taskEntity.getId() + 1, request)
                .andExpect(status().isNotFound());
    }


    // UPDATE STATUS ____________________________________________
    ResultActions updateStatusResultActions(final Integer id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    @WithMockUser(UPN)
    void updateStatus_200() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), null, projectEntity);
        taskRepository.save(taskEntity);

        TaskUpdateStatusReq request = new TaskUpdateStatusReq(TaskStatus.IN_PROCESS);

        updateStatusResultActions(taskEntity.getId(), request)
                .andExpect(status().isOk());

        TaskEntity actual = taskRepository.findById(taskEntity.getId()).orElse(new TaskEntity());
        assertEquals(TaskStatus.IN_PROCESS, actual.getStatus());
    }

    @Test
    @WithMockUser(UPN)
    void updateStatus_statusIgnoreCase_200() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), null, projectEntity);
        taskRepository.save(taskEntity);

        Map<String, Object> request = new HashMap<>();
        request.put("status", "in_proCess");

        updateStatusResultActions(taskEntity.getId(), request)
                .andExpect(status().isOk());

        TaskEntity actual = taskRepository.findById(taskEntity.getId()).orElse(new TaskEntity());
        assertEquals(TaskStatus.IN_PROCESS, actual.getStatus());
    }

    @Test
    @WithMockUser(UPN)
    void updateStatus_incorrectFlow_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), null, projectEntity);
        taskEntity.setStatus(TaskStatus.CLOSED);
        taskRepository.save(taskEntity);

        TaskUpdateStatusReq request = new TaskUpdateStatusReq(TaskStatus.IN_PROCESS);

        updateStatusResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void updateStatus_incorrectStatus_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), null, projectEntity);
        taskRepository.save(taskEntity);

        Map<String, Object> request = new HashMap<>();
        request.put("status", "process");

        updateStatusResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void updateStatus_requiredField_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), null, projectEntity);
        taskRepository.save(taskEntity);

        TaskUpdateStatusReq request = new TaskUpdateStatusReq();

        updateStatusResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void updateStatus_authorNotInTeam_400() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);
        EmployeeEntity authorEntity2 = genRandomEmployeeEntityAndSave("test@com.com");

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity2, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity2.getId(), null, projectEntity);
        taskRepository.save(taskEntity);

        TaskUpdateStatusReq request = new TaskUpdateStatusReq(TaskStatus.IN_PROCESS);

        updateStatusResultActions(taskEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(UPN)
    void updateStatus_taskNotFound_404() throws Exception {
        EmployeeEntity authorEntity = genRandomEmployeeEntityAndSave(UPN);

        ProjectEntity projectEntity = genRandomProjectEntity();
        Set<TeamEmbeddable> set = Set
                .of(new TeamEmbeddable(authorEntity, EmployeeRole.PROJECT_MANAGER));
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        TaskEntity taskEntity = genRandomTaskEntity(authorEntity.getId(), null, projectEntity);
        taskRepository.save(taskEntity);

        TaskUpdateStatusReq request = new TaskUpdateStatusReq(TaskStatus.IN_PROCESS);

        updateStatusResultActions(taskEntity.getId() + 1, request)
                .andExpect(status().isNotFound());
    }

    EmployeeEntity genRandomEmployeeEntityAndSave(String upn) {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        employeeEntity.setUpn(upn);
        return employeeRepository.save(employeeEntity);
    }

    EmployeeEntity genRandomEmployeeEntityAndSave() {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        return employeeRepository.save(employeeEntity);
    }
}
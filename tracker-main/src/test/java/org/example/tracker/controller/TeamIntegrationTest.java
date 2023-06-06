package org.example.tracker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.tracker.controller.BaseIntegrationTest;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeamIntegrationTest extends BaseIntegrationTest {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    EmployeeRepository employeeRepository;
    final String URL = "/v1/teams";

    @AfterEach
    void resetDB() {
        projectRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void add_200() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.ANALYST);

        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        List<TeamResp> actual = projectService.getAllTeamEmployee(projectEntity.getId());

        assertEquals(1, actual.size());
        assertEquals(actual.get(0).getEmployee(), modelMapper.toEmployeeResp(employeeEntity));
        assertEquals(actual.get(0).getRole(), request.getRole());
    }

    @Test
    void add_ignoreCaseRole_200() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        Map<String, Object> request = new HashMap<>();
        request.put("employeeId", employeeEntity.getId());
        request.put("role", "analYST");

        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        List<TeamResp> actual = projectService.getAllTeamEmployee(projectEntity.getId());

        assertEquals(1, actual.size());
        assertEquals(actual.get(0).getEmployee(), modelMapper.toEmployeeResp(employeeEntity));
        assertEquals(actual.get(0).getRole(), EmployeeRole.ANALYST);
    }

    @Test
    void add_duplicateRole_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        EmployeeEntity employeeEntity2 = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        employeeRepository.save(employeeEntity2);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.ANALYST);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        request = genTeamReq(employeeEntity2.getId(), EmployeeRole.ANALYST);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_duplicateEmployee_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.ANALYST);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        request = genTeamReq(employeeEntity.getId(), EmployeeRole.PROJECT_MANAGER);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_requiredField_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), null);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_deletedEmployee_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity(EmployeeStatus.DELETED);
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.TESTER);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_notExistsRole_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        Map<String, Object> request = new HashMap<>();
        request.put("employeeId", employeeEntity.getId());
        request.put("role", "man");

        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_notFoundEmployee_404() throws Exception {
        ProjectEntity projectEntity = genRandomProjectEntity();
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(1, EmployeeRole.TESTER);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void add_notFoundProject_404() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        employeeRepository.save(employeeEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.TESTER);
        mvc.perform(post(URL + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_200() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.TESTER);
        mvc.perform(post(URL + "/{id}", projectEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        mvc.perform(delete(URL + "/{id}/{emId}", projectEntity.getId(), employeeEntity.getId()))
                .andExpect(status().isOk());

        List<TeamResp> actual = projectService.getAllTeamEmployee(projectEntity.getId());
        assertEquals(0, actual.size());
    }

    @Test
    void delete_notFoundProject_404() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        employeeRepository.save(employeeEntity);

        mvc.perform(delete(URL + "/{id}/{emId}", 1, employeeEntity.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_notFoundEmployee_404() throws Exception {
        ProjectEntity projectEntity = genRandomProjectEntity();
        projectRepository.save(projectEntity);

        mvc.perform(delete(URL + "/{id}/{emId}", projectEntity.getId(), 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_notFoundEmployeeInTeam_404() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        mvc.perform(delete(URL + "/{id}/{emId}", projectEntity.getId(), employeeEntity.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_200() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        EmployeeEntity employeeEntity2 = genRandomEmployeeEntity();
        EmployeeEntity employeeEntity3 = genRandomEmployeeEntity();
        employeeRepository.save(employeeEntity);
        employeeRepository.save(employeeEntity2);
        employeeRepository.save(employeeEntity3);

        Set<TeamEmbeddable> set = new HashSet<>();
        set.add(new TeamEmbeddable(employeeEntity, EmployeeRole.TESTER));
        set.add(new TeamEmbeddable(employeeEntity2, EmployeeRole.ANALYST));

        ProjectEntity projectEntity = genRandomProjectEntity();
        projectEntity.setTeams(set);
        projectRepository.save(projectEntity);

        ProjectEntity projectEntity2 = genRandomProjectEntity();
        projectEntity.setTeams(Set.of(new TeamEmbeddable(employeeEntity3, EmployeeRole.PROJECT_MANAGER)));
        projectRepository.save(projectEntity2);

        String content = mvc.perform(get(URL + "/{id}", projectEntity.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<TeamResp> actual = mapper.readValue(content, new TypeReference<>() {});
        assertEquals(set.size(), actual.size());
    }

    @Test
    void getAll_notFoundProject_404() throws Exception {
        mvc.perform(get(URL + "{id}", 1))
                .andExpect(status().isNotFound());
    }
}
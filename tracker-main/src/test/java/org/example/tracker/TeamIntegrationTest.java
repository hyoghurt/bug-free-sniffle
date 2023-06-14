package org.example.tracker;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class TeamIntegrationTest extends Base {
    final String URL = "/teams";

    @Autowired
    ProjectService projectService;


    // ADD __________________________________________________
    ResultActions addResultActions(final Integer id, final Object body) throws Exception {
        return mvc.perform(post(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    void add_200() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.ANALYST);

        addResultActions(projectEntity.getId(), request)
                .andExpect(status().isOk());

        List<TeamResp> actual = projectService.getAllTeamEmployee(projectEntity.getId());

        assertEquals(1, actual.size());
        assertEquals(modelMapper.toEmployeeResp(employeeEntity), actual.get(0).getEmployee());
        assertEquals(request.getRole(), actual.get(0).getRole());
    }

    @Test
    void add_duplicateRoleInTeam_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        EmployeeEntity employeeEntity2 = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        projectEntity.setTeams(Set.of(new TeamEmbeddable(employeeEntity, EmployeeRole.ANALYST)));
        employeeRepository.save(employeeEntity);
        employeeRepository.save(employeeEntity2);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity2.getId(), EmployeeRole.ANALYST);

        addResultActions(projectEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_duplicateEmployeeInTeam_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        projectEntity.setTeams(Set.of(new TeamEmbeddable(employeeEntity, EmployeeRole.ANALYST)));
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.PROJECT_MANAGER);

        addResultActions(projectEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_deletedEmployee_400() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity(EmployeeStatus.DELETED);
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.TESTER);

        addResultActions(projectEntity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_notFoundEmployee_404() throws Exception {
        ProjectEntity projectEntity = genRandomProjectEntity();
        projectRepository.save(projectEntity);

        TeamReq request = genTeamReq(1, EmployeeRole.TESTER);

        addResultActions(projectEntity.getId(), request)
                .andExpect(status().isNotFound());
    }

    @Test
    void add_notFoundProject_404() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        employeeRepository.save(employeeEntity);

        TeamReq request = genTeamReq(employeeEntity.getId(), EmployeeRole.TESTER);

        addResultActions(1, request)
                .andExpect(status().isNotFound());
    }


    // DELETED __________________________________________________
    ResultActions deleteResultActions(final Integer id, final Integer employeeId) throws Exception {
        return mvc.perform(delete(URL + "/{id}/employees/{emId}", id, employeeId));
    }

    @Test
    void delete_200() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        projectEntity.setTeams(Set.of(new TeamEmbeddable(employeeEntity, EmployeeRole.ANALYST)));
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        deleteResultActions(projectEntity.getId(), employeeEntity.getId())
                .andExpect(status().isOk());

        List<TeamResp> actual = projectService.getAllTeamEmployee(projectEntity.getId());
        assertEquals(0, actual.size());
    }

    @Test
    void delete_notFoundProject_404() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        employeeRepository.save(employeeEntity);

        deleteResultActions(1, employeeEntity.getId())
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_notFoundEmployee_404() throws Exception {
        ProjectEntity projectEntity = genRandomProjectEntity();
        projectRepository.save(projectEntity);

        deleteResultActions(projectEntity.getId(), 1)
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_notFoundEmployeeInTeam_404() throws Exception {
        EmployeeEntity employeeEntity = genRandomEmployeeEntity();
        ProjectEntity projectEntity = genRandomProjectEntity();
        employeeRepository.save(employeeEntity);
        projectRepository.save(projectEntity);

        deleteResultActions(projectEntity.getId(), employeeEntity.getId())
                .andExpect(status().isNotFound());
    }


    // GET ALL __________________________________________________
    ResultActions getAllResultActions(final Integer id) throws Exception {
        return mvc.perform(get(URL + "/{id}", id));
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

        String content = getAllResultActions(projectEntity.getId())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<TeamResp> actual = mapper.readValue(content, new TypeReference<>() {
        });
        assertEquals(set.size(), actual.size());
        assertTrue(actual.stream()
                .noneMatch(t -> employeeEntity3.getId().equals(t.getEmployee().getId())));
    }

    @Test
    void getAll_notFoundProject_404() throws Exception {
        getAllResultActions(1)
                .andExpect(status().isNotFound());
    }
}
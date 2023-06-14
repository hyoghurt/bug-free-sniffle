package org.example.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.service.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {TeamController.class, ExceptionController.class})
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class})
class TeamControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private TeamService service;

    private final String URL = "/teams";


    // ADD __________________________________________________
    ResultActions addResultActions(final Object id, final Object body) throws Exception {
        return mvc.perform(post(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }

    // DELETED __________________________________________________
    ResultActions deleteResultActions(final Object id, final Object employeeId) throws Exception {
        return mvc.perform(delete(URL + "/{id}/employees/{emId}", id, employeeId));
    }

    // GET ALL __________________________________________________
    ResultActions getAllResultActions(final Object id) throws Exception {
        return mvc.perform(get(URL + "/{id}", id));
    }


    // success ______________________
    @Test
    void add_200() throws Exception {
        TeamReq request = genTeamReq();

        addResultActions(1, request)
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1)).addEmployeeToProject(1, request);
    }

    @Test
    void add_ignoreCaseRole_200() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("employeeId", 1);
        request.put("role", "analYST");

        addResultActions(1, request)
                .andExpect(status().isOk());
    }

    @Test
    void delete_200() throws Exception {
        deleteResultActions(1, 1)
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .removeEmployeeFromProject(1, 1);
    }

    @Test
    void getAll_200() throws Exception {
        getAllResultActions(1)
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1))
                .getProjectEmployees(1);
    }


    // required field ______________________________
    @Test
    void add_requiredField_400() throws Exception {
        TeamReq request = genTeamReq();
        request.setRole(null);

        addResultActions(1, request)
                .andExpect(status().isBadRequest());
    }


    // path Type Mismatch ______________________
    @Test
    void add_pathTypeMismatch_400() throws Exception {
        TeamReq request = genTeamReq();

        addResultActions("ab", request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_pathTypeMismatch_400() throws Exception {
        deleteResultActions(1, "ab")
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_pathTypeMismatch_400() throws Exception {
        getAllResultActions("ab")
                .andExpect(status().isBadRequest());
    }


    // enum not exists ______________________________
    @Test
    void add_notExistsRole_400() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("employeeId", 1);
        request.put("role", "man");

        addResultActions(1, request)
                .andExpect(status().isBadRequest());
    }


    TeamReq genTeamReq() {
        return TeamReq.builder()
                .employeeId(1)
                .role(EmployeeRole.ANALYST)
                .build();
    }
}
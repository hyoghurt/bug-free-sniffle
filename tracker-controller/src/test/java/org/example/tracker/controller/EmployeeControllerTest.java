package org.example.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.service.EmployeeService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@SpringBootTest(classes = {EmployeeController.class, ExceptionController.class})
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class})
@ActiveProfiles("dev")
class EmployeeControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private EmployeeService employeeService;

    private final String URL = "/v1/employee";

    // GET BY ID _________________________________________
    ResultActions getByIdResultActions(final Object id) throws Exception {
        return mvc.perform(get(URL + "/{id}", id));
    }

    // CREATE _________________________________________
    ResultActions createResultActions(final Object body) throws Exception {
        return mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }

    // UPDATE ______________________________________________________
    ResultActions updateResultActions(final Object id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }

    // DELETE ______________________________________________________
    ResultActions deleteResultActions(Object id) throws Exception {
        return mvc.perform(delete(URL + "/{id}", id));
    }

    // success ______________________
    @Test
    void create_201() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();
        EmployeeResp resp = genRandomEmployeeResp();
        Mockito.when(employeeService.create(request)).thenReturn(resp);

        createResultActions(request)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(resp)));

        Mockito.verify(employeeService, Mockito.times(1)).create(request);
    }

    @Test
    void getById_201() throws Exception {
        EmployeeResp resp = genRandomEmployeeResp();
        Mockito.when(employeeService.getById(1)).thenReturn(resp);

        getByIdResultActions(1)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(resp)));

        Mockito.verify(employeeService, Mockito.times(1)).getById(1);
    }

    @Test
    void update_200() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();
        EmployeeResp resp = genRandomEmployeeResp();
        Mockito.when(employeeService.update(1, request)).thenReturn(resp);

        updateResultActions(1, request)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(resp)));

        Mockito.verify(employeeService, Mockito.times(1)).update(1, request);
    }

    @Test
    void delete_200() throws Exception {
        deleteResultActions(1)
                .andExpect(status().isOk());

        Mockito.verify(employeeService, Mockito.times(1)).delete(1);
    }

    // invalid email ______________________
    @Test
    void create_invalidEmail_400() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();
        request.setEmail("username@.com");

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_invalidEmail_400() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();
        request.setEmail("username@.com");

        updateResultActions(1, request)
                .andExpect(status().isBadRequest());
    }

    // required field ______________________
    @Test
    void create_requiredField_400() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();
        request.setLastName(null);

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_requiredField_400() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();
        request.setLastName(null);

        updateResultActions(1, request)
                .andExpect(status().isBadRequest());
    }

    // path Type Mismatch ______________________
    @Test
    void getById_pathTypeMismatch_400() throws Exception {
        getByIdResultActions("ab")
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_pathTypeMismatch_400() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();

        updateResultActions("ab", request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_pathTypeMismatch_400() throws Exception {
        deleteResultActions("ab")
                .andExpect(status().isBadRequest());
    }

    EmployeeReq genRandomEmployeeReq() {
        return EmployeeReq.builder()
                .upn(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .build();
    }

    EmployeeResp genRandomEmployeeResp() {
        return EmployeeResp.builder()
                .upn(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .id(1)
                .status(EmployeeStatus.ACTIVE)
                .build();
    }
}
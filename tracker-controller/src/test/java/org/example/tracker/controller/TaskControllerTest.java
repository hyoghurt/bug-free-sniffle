package org.example.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskStatus;
import org.example.tracker.dto.task.TaskUpdateStatusReq;
import org.example.tracker.service.TaskService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {TaskController.class, ExceptionController.class})
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class})
class TaskControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private TaskService service;
    private final String URL = "/tasks";

    // CREATE ____________________________________________
    ResultActions createResultActions(final TaskReq body) throws Exception {
        return mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }
    // UPDATE ____________________________________________
    ResultActions updateResultActions(final Object id, final TaskReq body) throws Exception {
        return mvc.perform(put(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }
    // UPDATE STATUS ____________________________________________
    ResultActions updateStatusResultActions(final Object id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }


    // success ______________________
    @Test
    void create_201() throws Exception {
        TaskReq request = genRandomTaskReq();
        TaskResp resp = genRandomTaskResp();
        Mockito.when(service.create(request)).thenReturn(resp);

        createResultActions(request)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(resp)));

        Mockito.verify(service, Mockito.times(1)).create(request);
    }

    @Test
    void update_200() throws Exception {
        TaskReq request = genRandomTaskReq();
        TaskResp resp = genRandomTaskResp();
        Mockito.when(service.update(1, request)).thenReturn(resp);

        updateResultActions(1, request)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(resp)));

        Mockito.verify(service, Mockito.times(1)).update(1, request);
    }

    @Test
    void updateStatus_200() throws Exception {
        TaskUpdateStatusReq request = new TaskUpdateStatusReq(TaskStatus.IN_PROCESS);

        updateStatusResultActions(1, request)
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1)).updateStatus(1, request);
    }


    // enum ignore case _________________________________
    @Test
    void updateStatus_statusIgnoreCase_200() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("status", "in_proCess");

        updateStatusResultActions(1, request)
                .andExpect(status().isOk());
    }


    // enum not exists ______________________________
    @Test
    void updateStatus_incorrectStatus_400() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("status", "IN_DEV");

        updateStatusResultActions(1, request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByParam_incorrectStatus_400() throws Exception {
        mvc.perform(get(URL).param("statuses", "DRAFT"))
                .andExpect(status().isBadRequest());
    }


    // incorrect datetime syntax ______________________________
    @Test
    void getAllByParam_incorrectDatetime_400() throws Exception {
        mvc.perform(get(URL).param("minCreatedDatetime", "1:23:023"))
                .andExpect(status().isBadRequest());
    }


    // required field ______________________________
    @Test
    void create_requiredField_400() throws Exception {
        TaskReq request = genRandomTaskReq();
        request.setDeadlineDatetime(null);

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_requiredField_400() throws Exception {
        TaskReq request = genRandomTaskReq();
        request.setDeadlineDatetime(null);

        updateResultActions(1, request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_requiredField_400() throws Exception {
        TaskUpdateStatusReq request = new TaskUpdateStatusReq();

        updateStatusResultActions(1, request)
                .andExpect(status().isBadRequest());
    }


    // path Type Mismatch ______________________
    @Test
    void update_pathTypeMismatch_400() throws Exception {
        TaskReq request = genRandomTaskReq();

        updateResultActions("ab", request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_pathTypeMismatch_400() throws Exception {
        TaskUpdateStatusReq request = new TaskUpdateStatusReq(TaskStatus.IN_PROCESS);

        updateStatusResultActions("ab", request)
                .andExpect(status().isBadRequest());
    }


    // not valid deadline ______________________
    @Test
    void create_notValidDeadline_400() throws Exception {
        TaskReq request = genRandomTaskReq();
        request.setLaborCostsInHours(9);
        request.setDeadlineDatetime(Instant.now().plus(6, ChronoUnit.HOURS));

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_notValidDeadline_400() throws Exception {
        TaskReq request = genRandomTaskReq();
        request.setLaborCostsInHours(9);
        request.setDeadlineDatetime(Instant.now().plus(6, ChronoUnit.HOURS));

        updateResultActions(1, request)
                .andExpect(status().isBadRequest());
    }


    TaskReq genRandomTaskReq() {
        return TaskReq.builder()
                .title(UUID.randomUUID().toString())
                .projectId(1)
                .assigneesId(1)
                .laborCostsInHours(1)
                .deadlineDatetime(Instant.now().plus(6, ChronoUnit.HOURS))
                .build();
    }

    TaskResp genRandomTaskResp() {
        return TaskResp.builder()
                .id(1)
                .status(TaskStatus.IN_PROCESS)
                .title(UUID.randomUUID().toString())
                .projectId(1)
                .assigneesId(1)
                .laborCostsInHours(1)
                .deadlineDatetime(Instant.now().plus(6, ChronoUnit.HOURS))
                .build();
    }
}
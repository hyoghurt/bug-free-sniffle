package org.example.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;
import org.example.tracker.service.ProjectService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {ProjectController.class, ExceptionController.class})
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, DataSourceAutoConfiguration.class})
class ProjectControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ProjectService service;

    private final String URL = "/projects";


    // CREATE ______________________________________________
    ResultActions createResultActions(final Object body) throws Exception {
        return mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }

    // UPDATE ____________________________________________________
    ResultActions updateResultActions(final Object id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }

    // UPDATE STATUS _____________________________________________________
    ResultActions updateStatusResultActions(final Object id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(body)));
    }

    // GET ALL BY PARAM __________________________________________________
    ResultActions getAllByParamResultActions(final String query, final List<String> statuses) throws Exception {
        String[] array = (statuses != null) ? statuses.toArray(String[]::new) : null;

        MockHttpServletRequestBuilder requestBuilder = get(URL);
        if (query != null) requestBuilder.param("query", query);
        if (statuses != null) requestBuilder.param("statuses", array);

        return mvc.perform(requestBuilder);
    }

    // success ______________________
    @Test
    void create_201() throws Exception {
        ProjectReq request = genRandomProjectReq();
        ProjectResp resp = genRandomProjectResp();
        Mockito.when(service.create(request)).thenReturn(resp);

        createResultActions(request)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(resp)));

        Mockito.verify(service, Mockito.times(1)).create(request);
    }

    @Test
    void update_200() throws Exception {
        ProjectReq request = genRandomProjectReq();
        ProjectResp resp = genRandomProjectResp();
        Mockito.when(service.update(1, request)).thenReturn(resp);

        updateResultActions(1, request)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(resp)));

        Mockito.verify(service, Mockito.times(1)).update(1, request);
    }

    @Test
    void updateStatus_200() throws Exception {
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq(ProjectStatus.IN_DEVELOPMENT);

        updateStatusResultActions(1, request)
                .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1)).updateStatus(1, request);
    }


    @Test
    void getAllByParam_200() throws Exception {
        ProjectResp resp = genRandomProjectResp();
        Mockito.when(service.getAllByParam(Mockito.any())).thenReturn(List.of(resp));

        getAllByParamResultActions("hello", List.of("DRAFT"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(List.of(resp))));

        Mockito.verify(service, Mockito.times(1)).getAllByParam(Mockito.any());
    }

    @Test
    void getAllByParam_paramNull_200() throws Exception {
        ProjectResp resp = genRandomProjectResp();
        Mockito.when(service.getAllByParam(Mockito.any())).thenReturn(List.of(resp));

        getAllByParamResultActions(null, null)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(mapper.writeValueAsString(List.of(resp))));

        Mockito.verify(service, Mockito.times(1)).getAllByParam(Mockito.any());
    }


    // enum ignore case _________________________________
    @Test
    void updateStatus_ignoreCase_200() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("status", "IN_testing");

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
        getAllByParamResultActions("hello", List.of("DRAF"))
                .andExpect(status().isBadRequest());
    }


    // required field ______________________________
    @Test
    void create_requiredField_400() throws Exception {
        ProjectReq request = genRandomProjectReq();
        request.setCode(null);

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_requiredField_400() throws Exception {
        ProjectReq request = genRandomProjectReq();
        request.setCode(null);

        updateResultActions(1, request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_requiredField_400() throws Exception {
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq();

        updateStatusResultActions(1, request)
                .andExpect(status().isBadRequest());
    }


    // path Type Mismatch ______________________
    @Test
    void update_pathTypeMismatch_400() throws Exception {
        ProjectReq request = genRandomProjectReq();

        updateResultActions("ab", request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_pathTypeMismatch_400() throws Exception {
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq(ProjectStatus.IN_DEVELOPMENT);

        updateStatusResultActions("ab", request)
                .andExpect(status().isBadRequest());
    }



    ProjectReq genRandomProjectReq() {
        return ProjectReq.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .build();
    }

    ProjectResp genRandomProjectResp() {
        return ProjectResp.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .status(ProjectStatus.IN_DEVELOPMENT)
                .id(1)
                .build();
    }
}
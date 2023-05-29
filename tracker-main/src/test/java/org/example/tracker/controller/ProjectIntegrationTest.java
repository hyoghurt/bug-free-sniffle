package org.example.tracker.controller;

import org.example.tracker.ProjectFilter;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class ProjectIntegrationTest extends BaseIntegrationTest {
    final String URL = "/v1/project";


    // CREATE ______________________________________________
    ResultActions createResultActions(final Object body) throws Exception {
        return mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    void create_201() throws Exception {
        ProjectReq request = genRandomProjectReq();

        String content = createResultActions(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProjectResp actual = mapper.readValue(content, ProjectResp.class);
        ProjectEntity entity = projectRepository.findById(actual.getId()).orElse(new ProjectEntity());
        assertEquals(modelMapper.toProjectResp(entity), actual);
        assertEquals(actual.getStatus(), ProjectStatus.DRAFT);
    }

    @Test
    void create_duplicateCode_400() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity());
        ProjectReq request = genProjectReq(entity.getCode(), "name");

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_requiredField_400() throws Exception {
        ProjectReq request = genProjectReq(null, "name");

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }



    // UPDATE ____________________________________________________
    ResultActions updateResultActions(final Integer id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    void update_200() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity());
        ProjectReq request = genRandomProjectReq();

        updateResultActions(entity.getId(), request)
                .andExpect(status().isOk());

        ProjectEntity updateEntity = projectRepository.findById(entity.getId()).orElse(new ProjectEntity());
        assertEquals(request.getCode(), updateEntity.getCode());
        assertEquals(request.getName(), updateEntity.getName());
    }

    @Test
    void update_notFound_404() throws Exception {
        ProjectReq request = genRandomProjectReq();

        updateResultActions(1, request)
                .andExpect(status().isNotFound());
    }

    @Test
    void update_duplicateCode_400() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity());
        ProjectEntity entity2 = projectRepository.save(genRandomProjectEntity());
        ProjectReq request = genProjectReq(entity.getCode(), "name");

        updateResultActions(entity2.getId(), request)
                .andExpect(status().isBadRequest());
    }



    // UPDATE STATUS _____________________________________________________
    ResultActions updateStatusResultActions(final Integer id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    void updateStatus_200() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity());
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq(ProjectStatus.IN_DEVELOPMENT);

        updateStatusResultActions(entity.getId(), request)
                .andExpect(status().isOk());

        ProjectEntity updateEntity = projectRepository.findById(entity.getId()).orElse(new ProjectEntity());
        assertEquals(request.getStatus(), updateEntity.getStatus());
    }

    @Test
    void updateStatus_ignoreCase_200() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity());
        Map<String, String> request = new HashMap<>();
        request.put("status", "IN_testing");

        updateStatusResultActions(entity.getId(), request)
                .andExpect(status().isOk());

        ProjectEntity updateEntity = projectRepository.findById(entity.getId()).orElse(new ProjectEntity());
        assertEquals(request.get("status").toUpperCase(), updateEntity.getStatus().name().toUpperCase());
    }

    @Test
    void updateStatus_incorrectFlow_400() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity(ProjectStatus.FINISHED));
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq(ProjectStatus.IN_DEVELOPMENT);

        updateStatusResultActions(entity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_incorrectStatus_400() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity());
        Map<String, String> request = new HashMap<>();
        request.put("status", "IN_DEV");

        updateStatusResultActions(entity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_requiredField_400() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity());
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq();

        updateStatusResultActions(entity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_notFound_404() throws Exception {
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq(ProjectStatus.IN_DEVELOPMENT);

        updateStatusResultActions(1, request)
                .andExpect(status().isNotFound());
    }


    // GET ALL BY PARAM __________________________________________________
    void getAllByParam(final String query, final List<ProjectStatus> statuses) throws Exception {
        List<ProjectEntity> entities = initProjectEntities();

        List<ProjectResp> expected = ProjectFilter.filter(entities, query, statuses).stream()
                .map(modelMapper::toProjectResp)
                .toList();

        String[] array = (statuses != null) ? statuses.stream()
                .map(ProjectStatus::name)
                .toArray(String[]::new) : null;

        MockHttpServletRequestBuilder requestBuilder = get(URL + "s");
        if (query != null) requestBuilder.param("query", query);
        if (statuses != null) requestBuilder.param("statuses", array);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    @Test
    void getAllByParam_paramNull_200() throws Exception {
        getAllByParam(null, null);
    }

    @Test
    void getAllByParam_paramQuery_200() throws Exception {
        final String search = "test";
        getAllByParam(search, null);
    }

    @Test
    void getAllByParam_paramStatuses_200() throws Exception {
        List<ProjectStatus> statuses = List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED);
        getAllByParam(null, statuses);
    }

    @Test
    void getAllByParam_paramQueryAndStatuses_200() throws Exception {
        final String search = "test";
        List<ProjectStatus> statuses = List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED);
        getAllByParam(search, statuses);
    }
}
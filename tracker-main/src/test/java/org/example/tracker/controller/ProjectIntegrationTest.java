package org.example.tracker.controller;

import org.example.tracker.controller.BaseIntegrationTest;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dto.project.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectIntegrationTest extends BaseIntegrationTest {
    @Autowired
    ProjectRepository repository;
    final String URL = "/v1/projects";

    @AfterEach
    void resetDB() {
        repository.deleteAll();
    }

    @Test
    void create_201() throws Exception {
        ProjectReq request = genRandomProjectReq();

        String content = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProjectResp actual = mapper.readValue(content, ProjectResp.class);
        ProjectEntity entity = repository.findById(actual.getId()).orElse(new ProjectEntity());
        assertEquals(modelMapper.toProjectResp(entity), actual);
        assertEquals(actual.getStatus(), ProjectStatus.DRAFT);
    }

    @Test
    void create_duplicateCode_400() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity());
        ProjectReq request = genProjectReq(entity.getCode(), "name");

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_requiredField_400() throws Exception {
        ProjectReq request = genProjectReq(null, "name");

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_200() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity());
        ProjectReq request = genRandomProjectReq();

        mvc.perform(put(URL + "/{id}", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        ProjectEntity updateEntity = repository.findById(entity.getId()).orElse(new ProjectEntity());
        assertEquals(request.getCode(), updateEntity.getCode());
        assertEquals(request.getName(), updateEntity.getName());
    }

    @Test
    void update_404() throws Exception {
        ProjectReq request = genRandomProjectReq();

        mvc.perform(put(URL + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_duplicateCode_400() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity());
        ProjectEntity entity2 = repository.save(genRandomProjectEntity());
        ProjectReq request = genProjectReq(entity.getCode(), "name");

        mvc.perform(put(URL + "/{id}", entity2.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_200() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity());
        ProjectUpdateStatusReq request = ProjectUpdateStatusReq.builder()
                .status(ProjectStatus.IN_DEVELOPMENT)
                .build();

        mvc.perform(put(URL + "/{id}/status", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        ProjectEntity updateEntity = repository.findById(entity.getId()).orElse(new ProjectEntity());
        assertEquals(request.getStatus(), updateEntity.getStatus());
    }

    @Test
    void updateStatus_ignoreCase_200() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity());
        Map<String, String> request = new HashMap<>();
        request.put("status", "IN_testing");

        mvc.perform(put(URL + "/{id}/status", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        ProjectEntity updateEntity = repository.findById(entity.getId()).orElse(new ProjectEntity());
        assertEquals(request.get("status").toUpperCase(), updateEntity.getStatus().name().toUpperCase());
    }

    @Test
    void updateStatus_incorrect_flow_400() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity(ProjectStatus.FINISHED));
        ProjectUpdateStatusReq request = ProjectUpdateStatusReq.builder()
                .status(ProjectStatus.IN_DEVELOPMENT)
                .build();

        mvc.perform(put(URL + "/{id}/status", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_invalidateStatus_400() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity());
        Map<String, String> request = new HashMap<>();
        request.put("status", "IN_DEV");

        mvc.perform(put(URL + "/{id}/status", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_required_field_400() throws Exception {
        ProjectEntity entity = repository.save(genRandomProjectEntity());
        ProjectUpdateStatusReq request = ProjectUpdateStatusReq.builder()
                .build();

        mvc.perform(put(URL + "/{id}/status", entity.getId())
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_not_found_404() throws Exception {
        ProjectUpdateStatusReq request = ProjectUpdateStatusReq.builder()
                .status(ProjectStatus.IN_DEVELOPMENT)
                .build();

        mvc.perform(put(URL + "/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByFilter_empty_200() throws Exception {
        List<ProjectEntity> entities = initEntity();
        List<ProjectResp> expected = myFilter(entities, new ProjectFilterParam(null, null));

        mvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    @Test
    void getAllByFilter_code_name_200() throws Exception {
        List<ProjectEntity> entities = initEntity();

        String search = "test";
        ProjectFilterParam filter = ProjectFilterParam.builder()
                .query(search)
                .build();
        List<ProjectResp> expected = myFilter(entities, filter);

        mvc.perform(get(URL).param("query", filter.getQuery()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    @Test
    void getAllByFilter_statuses_200() throws Exception {
        List<ProjectEntity> entities = initEntity();

        List<ProjectStatus> statuses = List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED);
        ProjectFilterParam filter = ProjectFilterParam.builder()
                .statuses(statuses)
                .build();
        List<ProjectResp> expected = myFilter(entities, filter);

        String[] array = filter.getStatuses().stream()
                .map(ProjectStatus::name)
                .toArray(String[]::new);

        mvc.perform(get(URL)
                        .param("statuses", array))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    @Test
    void getAllByFilter_code_name_statuses_200() throws Exception {
        List<ProjectEntity> entities = initEntity();

        String search = "test";
        List<ProjectStatus> statuses = List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED);
        ProjectFilterParam filter = ProjectFilterParam.builder()
                .query(search)
                .statuses(statuses)
                .build();

        List<ProjectResp> expected = myFilter(entities, filter);
        String[] array = filter.getStatuses().stream()
                .map(ProjectStatus::name)
                .toArray(String[]::new);

        mvc.perform(get(URL)
                        .param("statuses", array)
                        .param("query", filter.getQuery()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    List<ProjectResp> myFilter(List<ProjectEntity> entities, ProjectFilterParam filter) {
        return entities.stream().filter(e ->
                (
                        filter.getQuery() == null
                                || e.getCode().toUpperCase().contains(filter.getQuery().toUpperCase())
                                || e.getName().toUpperCase().contains(filter.getQuery().toUpperCase())
                ) && (
                        filter.getStatuses() == null
                                || filter.getStatuses().stream().anyMatch(s -> e.getStatus().equals(s))
                )
        ).map(modelMapper::toProjectResp).toList();
    }

    List<ProjectEntity> initEntity() {
        List<ProjectEntity> entities = new ArrayList<>();
        System.out.println("CREATE PROJECT ENTITIES--------------------------");
        entities.add(repository.save(genProjectEntity("code20", "name", "desc", "DRAFT")));
        entities.add(repository.save(genProjectEntity("code21", "name", "desc", "DRAFT")));
        entities.add(repository.save(genProjectEntity("code22", "name", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code_test", "name", "desc", "DRAFT")));
        entities.add(repository.save(genProjectEntity("test_code", "name", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("ctestc", "name", "desc", "FINISHED")));
        entities.add(repository.save(genProjectEntity("cteSTc", "name", "desc", "FINISHED")));
        entities.add(repository.save(genProjectEntity("test", "name", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code1", "test", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code2", "testname", "desc", "FINISHED")));
        entities.add(repository.save(genProjectEntity("code3", "nameTest", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code43", "name", null, "DRAFT")));
        return entities;
    }
}
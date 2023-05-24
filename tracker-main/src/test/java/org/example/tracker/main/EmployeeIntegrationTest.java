package org.example.tracker.main;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeIntegrationTest extends BaseIntegrationTest {
    @Autowired
    EmployeeRepository repository;
    final String URL = "/v1/employees";

    @AfterEach
    void resetDB() {
        repository.deleteAll();
    }

    @Test
    void getById_200() throws Exception {
        EmployeeEntity entity = genRandomEmployeeEntity();
        Integer id = repository.save(entity).getId();

        mvc.perform(get(URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json(asJsonString(modelMapper.toEmployeeResp(entity))));
    }

    @Test
    void getById_404() throws Exception {
        mvc.perform(get(URL + "/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_201() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();

        String content = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        EmployeeResp actual = mapper.readValue(content, EmployeeResp.class);
        EmployeeEntity entity = repository.findById(actual.getId()).orElse(new EmployeeEntity());
        assertEquals(modelMapper.toEmployeeResp(entity), actual);
    }

    @Test
    void create_duplicateUpn_400() throws Exception {
        EmployeeEntity entity = repository.save(genRandomEmployeeEntity());
        EmployeeReq request = genEmployeeReq(entity.getUpn(), "first", "last");

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_requiredField_400() throws Exception {
        EmployeeReq request = genEmployeeReq(UUID.randomUUID().toString(), null, "last");

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_200() throws Exception {
        EmployeeEntity entity = repository.save(genRandomEmployeeEntity());
        EmployeeReq request = genRandomEmployeeReq();

        mvc.perform(put(URL + "/{id}", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        EmployeeEntity updateEntity = repository.findById(entity.getId()).orElse(new EmployeeEntity());
        assertEquals(request.getUpn(), updateEntity.getUpn());
        assertEquals(request.getFirstName(), updateEntity.getFirstName());
        assertEquals(request.getLastName(), updateEntity.getLastName());
    }

    @Test
    void update_404() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();

        mvc.perform(put(URL + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_duplicateUpn_400() throws Exception {
        EmployeeEntity entity = repository.save(genRandomEmployeeEntity());
        EmployeeEntity entity2 = repository.save(genRandomEmployeeEntity());
        EmployeeReq request = genEmployeeReq(entity.getUpn(), "first", "last");

        mvc.perform(put(URL + "/{id}", entity2.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_alreadyDeleted_400() throws Exception {
        EmployeeEntity entity = repository.save(genRandomEmployeeEntity(EmployeeStatus.DELETED));
        EmployeeReq request = genRandomEmployeeReq();

        mvc.perform(put(URL + "/{id}", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_200() throws Exception {
        EmployeeEntity entity = repository.save(genRandomEmployeeEntity());

        mvc.perform(delete(URL + "/{id}", entity.getId()))
                .andExpect(status().isOk());

        EmployeeEntity deleteEntity = repository.findById(entity.getId()).orElse(new EmployeeEntity());
        assertEquals(EmployeeStatus.DELETED, deleteEntity.getStatus());
    }

    @Test
    void delete_404() throws Exception {
        mvc.perform(delete(URL + "/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_200() throws Exception {
        List<EmployeeEntity> entities = initEntity();
        List<EmployeeResp> expected = myFilter(entities, null);

        mvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    @Test
    void getAllBySearch_200() throws Exception {
        final String SEARCH = "tEst";
        List<EmployeeEntity> entities = initEntity();
        List<EmployeeResp> expected = myFilter(entities, SEARCH);

        mvc.perform(get(URL).param("query", SEARCH))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    List<EmployeeResp> myFilter(List<EmployeeEntity> entities, String search) {
        return entities.stream().filter(e ->
                (
                        search == null
                                || e.getFirstName().toUpperCase().contains(search.toUpperCase())
                                || e.getLastName().toUpperCase().contains(search.toUpperCase())
                                || (e.getMiddleName() != null && e.getMiddleName().toUpperCase().contains(search.toUpperCase()))
                                || (e.getEmail() != null && e.getEmail().toUpperCase().contains(search.toUpperCase()))
                                || (e.getUpn() != null && e.getUpn().toUpperCase().contains(search.toUpperCase()))
                ) && (
                        e.getStatus().equals(EmployeeStatus.ACTIVE)
                )
        ).map(modelMapper::toEmployeeResp).toList();
    }

    List<EmployeeEntity> initEntity() {
        List<EmployeeEntity> entities = new ArrayList<>();
        System.out.println("CREATE Employee ENTITIES--------------------------");
        entities.add(repository.save(genEmployeeEntity("testName", "last", null, null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "lastTest", null, null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", "midtestname", null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", null, "testmail", null, null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", null, null, "testupn", null, EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("name", "last", null, null, null, "postest", EmployeeStatus.ACTIVE)));
        entities.add(repository.save(genEmployeeEntity("testFirst", "testLast", "testMiddle", "testEmail", "testUpn", "testPosition", EmployeeStatus.DELETED)));
        return entities;
    }
}
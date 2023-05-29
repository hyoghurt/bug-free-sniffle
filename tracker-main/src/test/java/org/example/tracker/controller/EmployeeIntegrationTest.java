package org.example.tracker.controller;

import org.example.tracker.EmployeeFilter;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class EmployeeIntegrationTest extends BaseIntegrationTest {
    final String URL = "/v1/employee";


    // GET BY ID _________________________________________
    ResultActions getByIdResultActions(final Object id) throws Exception {
        return mvc.perform(get(URL + "/{id}", id));
    }

    @Test
    void getById_200() throws Exception {
        EmployeeEntity entity = genRandomEmployeeEntity();
        Integer id = employeeRepository.save(entity).getId();

        getByIdResultActions(id)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json(asJsonString(modelMapper.toEmployeeResp(entity))));
    }

    @Test
    void getById_notFound_404() throws Exception {
        getByIdResultActions(1)
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_pathTypeMismatch_400() throws Exception {
        getByIdResultActions("ab")
                .andExpect(status().isBadRequest());
    }



    // CREATE ______________________________________________________
    ResultActions createResultActions(final Object body) throws Exception {
        return mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    void create_201() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();

        String content = createResultActions(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        EmployeeResp actual = mapper.readValue(content, EmployeeResp.class);
        EmployeeEntity entity = employeeRepository.findById(actual.getId()).orElse(new EmployeeEntity());
        assertEquals(modelMapper.toEmployeeResp(entity), actual);
    }

    @Test
    void create_incorrectEmailSyntax_400() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();
        request.setEmail("username@.com");

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_duplicateUpn_400() throws Exception {
        EmployeeEntity entity = employeeRepository.save(genRandomEmployeeEntity());
        EmployeeReq request = genEmployeeReq(entity.getUpn(), "first", "last");

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_requiredField_400() throws Exception {
        EmployeeReq request = genEmployeeReq(UUID.randomUUID().toString(), null, "last");

        createResultActions(request)
                .andExpect(status().isBadRequest());
    }



    // UPDATE ______________________________________________________
    ResultActions updateResultActions(final Object id, final Object body) throws Exception {
        return mvc.perform(put(URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(body)));
    }

    @Test
    void update_200() throws Exception {
        EmployeeEntity entity = employeeRepository.save(genRandomEmployeeEntity());
        EmployeeReq request = genRandomEmployeeReq();

        updateResultActions(entity.getId(), request)
                .andExpect(status().isOk());

        EmployeeEntity updateEntity = employeeRepository.findById(entity.getId()).orElse(new EmployeeEntity());
        assertEquals(request.getUpn(), updateEntity.getUpn());
        assertEquals(request.getFirstName(), updateEntity.getFirstName());
        assertEquals(request.getLastName(), updateEntity.getLastName());
    }

    @Test
    void update_notFound_404() throws Exception {
        EmployeeReq request = genRandomEmployeeReq();

        updateResultActions(1, request)
                .andExpect(status().isNotFound());
    }

    @Test
    void update_duplicateUpn_400() throws Exception {
        EmployeeEntity entity = employeeRepository.save(genRandomEmployeeEntity());
        EmployeeEntity entity2 = employeeRepository.save(genRandomEmployeeEntity());
        EmployeeReq request = genEmployeeReq(entity.getUpn(), "first", "last");

        updateResultActions(entity2.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_alreadyDeleted_400() throws Exception {
        EmployeeEntity entity = employeeRepository.save(genRandomEmployeeEntity(EmployeeStatus.DELETED));
        EmployeeReq request = genRandomEmployeeReq();

        updateResultActions(entity.getId(), request)
                .andExpect(status().isBadRequest());
    }



    // DELETE ______________________________________________________
    ResultActions deleteResultActions(Object id) throws Exception {
        return mvc.perform(delete(URL + "/{id}", id));
    }

    @Test
    void delete_200() throws Exception {
        EmployeeEntity entity = employeeRepository.save(genRandomEmployeeEntity());

        deleteResultActions(entity.getId())
                .andExpect(status().isOk());

        EmployeeEntity deleteEntity = employeeRepository.findById(entity.getId()).orElse(new EmployeeEntity());
        assertEquals(EmployeeStatus.DELETED, deleteEntity.getStatus());
    }

    @Test
    void delete_notFound_404() throws Exception {
        deleteResultActions(1)
                .andExpect(status().isNotFound());
    }



    // GET ALL BY PARAM ______________________________________________________
    void getAllByParam(final String query) throws Exception {
        List<EmployeeEntity> entities = initEmployeeEntities();
        List<EmployeeResp> expected = EmployeeFilter.filter(entities, query).stream()
                .map(modelMapper::toEmployeeResp)
                .toList();

        MockHttpServletRequestBuilder requestBuilder = get(URL + "s");
        if (query != null) requestBuilder.param("query", query);

        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(asJsonString(expected)));
    }

    @Test
    void getAllByParam_paramNull_200() throws Exception {
        getAllByParam(null);
    }

    @Test
    void getAllByParam_paramEmptyString_200() throws Exception {
        getAllByParam("");
    }

    @Test
    void getAllByParam_paramQuery_200() throws Exception {
        getAllByParam("tEst");
    }
}
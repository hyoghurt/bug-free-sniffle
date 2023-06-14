package org.example.tracker;

import org.example.tracker.entity.ProjectEntity;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
class ProjectIntegrationTest extends Base {
    final String URL = "/projects";


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
    void updateStatus_incorrectFlow_400() throws Exception {
        ProjectEntity entity = projectRepository.save(genRandomProjectEntity(ProjectStatus.FINISHED));
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq(ProjectStatus.IN_DEVELOPMENT);

        updateStatusResultActions(entity.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_notFound_404() throws Exception {
        ProjectUpdateStatusReq request = new ProjectUpdateStatusReq(ProjectStatus.IN_DEVELOPMENT);

        updateStatusResultActions(1, request)
                .andExpect(status().isNotFound());
    }
}
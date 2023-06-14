package org.example.tracker.service;

import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.entity.ProjectEntity;
import org.example.tracker.entity.TeamEmbeddable;

import java.util.List;

public interface ProjectService {
    ProjectResp create(ProjectReq request);

    ProjectResp update(Integer id, ProjectReq request);

    List<ProjectResp> getAllByParam(ProjectFilterParam param);

    void updateStatus(Integer id, ProjectUpdateStatusReq request);

    ProjectEntity getProjectEntity(Integer id);

    void addEmployee(Integer projectId, TeamEmbeddable teamEmbeddable);

    void removeEmployee(Integer projectId, Integer employeeId);

    List<TeamResp> getAllTeamEmployee(Integer projectId);

    boolean isInTeam(ProjectEntity projectEntity, Integer employeeId);

    boolean isInTeam(ProjectEntity projectEntity, String upn);
}

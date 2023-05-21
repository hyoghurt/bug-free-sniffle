package org.example.tracker.service;

import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;

import java.util.List;

public interface ProjectService {
    void create(ProjectReq request);

    void update(Integer id, ProjectReq request);

    List<ProjectResp> findByParam(ProjectFilterParam param);

    void updateStatus(Integer id, ProjectUpdateStatusReq request);

    void addEmployee(Integer projectId, TeamEmbeddable team);

    void removeEmployee(Integer projectId, Integer employeeId);

    List<EmployeeResp> getAllEmployee(Integer projectId);
}

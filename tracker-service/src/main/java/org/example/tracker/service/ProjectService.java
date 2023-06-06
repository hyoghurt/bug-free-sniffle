package org.example.tracker.service;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamResp;

import java.util.List;

public interface ProjectService {
    ProjectResp create(ProjectReq request);

    ProjectResp update(Integer id, ProjectReq request);

    List<ProjectResp> getAllByFilter(ProjectFilterParam filter);

    void updateStatus(Integer id, ProjectUpdateStatusReq request);

    ProjectEntity getProjectEntity(Integer id);

    /**
     * добавить сотрудника в команду проекта
     *
     * @param projectId идентификатор проекта
     */
    void addEmployee(Integer projectId, EmployeeEntity employee, EmployeeRole role);

    /**
     * удалить сотрудника из команды проекта
     *
     * @param projectId  идентификатор проекта
     * @param employeeId идентификатор сотрудника
     */
    void removeEmployee(Integer projectId, Integer employeeId);

    /**
     * получить всех сотрудников проекта
     *
     * @param projectId идентификатор проекта
     */
    List<TeamResp> getAllTeamEmployee(Integer projectId);

    boolean isInTeam(ProjectEntity projectEntity, Integer employeeId);
}

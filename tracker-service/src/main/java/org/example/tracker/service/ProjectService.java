package org.example.tracker.service;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;
import org.example.tracker.dto.team.EmployeeRole;

import java.util.List;

public interface ProjectService {
    ProjectResp create(ProjectReq request);

    ProjectResp update(Integer id, ProjectReq request);

    List<ProjectResp> findByParam(ProjectFilterParam param);

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
    List<EmployeeResp> getAllEmployee(Integer projectId);

    boolean isInTeam(ProjectEntity projectEntity, Integer employeeId);
}

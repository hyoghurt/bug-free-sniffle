package org.example.tracker.service;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;

import java.util.List;

public interface EmployeeService {
    EmployeeResp getById(Integer id);
    EmployeeResp getByUpn(String upn);
    void create(EmployeeReq request);
    void update(Integer id, EmployeeReq request);
    void delete(Integer id);
    List<EmployeeResp> find(String query);
    EmployeeEntity getEmployeeEntity(Integer id);
}

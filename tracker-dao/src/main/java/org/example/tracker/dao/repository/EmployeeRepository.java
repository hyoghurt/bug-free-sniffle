package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.EmployeeEntity;

import java.util.List;

public interface EmployeeRepository {
    EmployeeEntity create(EmployeeEntity entity);
    EmployeeEntity update(EmployeeEntity entity);
    EmployeeEntity getById(Integer id);
    List<EmployeeEntity> getAll();
    void deleteById(Integer id);
}

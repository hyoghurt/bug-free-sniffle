package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.EmployeeEntity;

import java.util.List;

public interface EmployeeRepositoryCustom {
    public List<EmployeeEntity> findAllByQuery(String query);
}

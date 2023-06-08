package org.example.tracker.repository.helper;

import org.example.tracker.entity.EmployeeEntity;
import org.example.tracker.dto.employee.EmployeeFilterParam;
import org.example.tracker.dto.employee.EmployeeStatus;

public class EmployeeFilter {
    public static boolean filter(EmployeeEntity e, EmployeeFilterParam param) {
        return (
                param.getQuery() == null
                        || e.getFirstName().toUpperCase().contains(param.getQuery().toUpperCase())
                        || e.getLastName().toUpperCase().contains(param.getQuery().toUpperCase())
                        || (e.getMiddleName() != null && e.getMiddleName().toUpperCase().contains(param.getQuery().toUpperCase()))
                        || (e.getEmail() != null && e.getEmail().toUpperCase().contains(param.getQuery().toUpperCase()))
                        || (e.getUpn() != null && e.getUpn().toUpperCase().contains(param.getQuery().toUpperCase()))
        ) && (
                e.getStatus().equals(EmployeeStatus.ACTIVE)
        );
    }
}
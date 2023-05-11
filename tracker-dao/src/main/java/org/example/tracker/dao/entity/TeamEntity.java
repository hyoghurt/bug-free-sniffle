package org.example.tracker.dao.entity;

import org.example.tracker.dao.enums.EmployeeRole;

public class TeamEntity {
    private int projectId; //required
    private int employeeId; //required
    private EmployeeRole role; //required
}

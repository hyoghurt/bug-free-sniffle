package org.example.tracker.dto.team;


// добавить сотрудника в команду проекта
public class TeamReq {
    private int projectId; //required
    private int employeeId; //required
    private EmployeeRole role; //required
}

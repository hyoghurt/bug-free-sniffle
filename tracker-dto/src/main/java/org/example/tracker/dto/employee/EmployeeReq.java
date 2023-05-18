package org.example.tracker.dto.employee;

import lombok.Data;

// создание сотрудника
// изменение сотрудника
@Data
public class EmployeeReq {
    private String firstName; //required
    private String lastName; //required
    private String middleName;
    private String position;
    private String upn;
    private String email;
}

package org.example.tracker.dao.entity;

import lombok.Builder;
import lombok.Data;
import org.example.tracker.dto.employee.EmployeeStatus;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class EmployeeEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 42L;

    private int id; //fk
    private String firstName; //required
    private String lastName; //required
    private String middleName;
    private String position;
    private String upn;
    private String email;
    private EmployeeStatus status; //required
}

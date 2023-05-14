package org.example.tracker.dao.entity;

import lombok.Data;
import org.example.tracker.dao.enums.EmployeeStatus;

import java.io.Serial;
import java.io.Serializable;

@Data
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

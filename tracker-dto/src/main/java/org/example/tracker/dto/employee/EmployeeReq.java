package org.example.tracker.dto.employee;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeeReq {
    @NotBlank(message = "firstName required")
    private String firstName;
    @NotBlank(message = "lastName required")
    private String lastName;
    private String middleName;
    private String position;
    private String upn;
    private String email;
}

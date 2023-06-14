package org.example.tracker.dto.employee;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class EmployeeResp extends EmployeeReq {

    @Schema(description = "уникальный идентификатор", example = "321")
    private int id;

    private EmployeeStatus status;
}

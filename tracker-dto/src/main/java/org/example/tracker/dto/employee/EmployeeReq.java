package org.example.tracker.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmployeeReq {
    @Schema(description = "имя", example = "иван")
    @NotBlank(message = "firstName required")
    @Size(max = 64, message = "length <= 64")
    private String firstName;

    @Schema(description = "фамилия", example = "иванов")
    @NotBlank(message = "lastName required")
    @Size(max = 64, message = "length <= 64")
    private String lastName;

    @Schema(description = "отчество", example = "иванович")
    @Size(max = 64, message = "length <= 64")
    private String middleName;

    @Schema(description = "должность", example = "лесоруб")
    @Size(max = 64, message = "length <= 64")
    private String position;

    @Schema(description = "учетная запись, но уникальное значение среди активных сотрудников",
            example = "dvornik@dom.com")
    @Size(max = 128, message = "length <= 128")
    private String upn;

    @Schema(description = "электронная почта", example = "dvornik@yandex.ru")
    @Email(message = "email is not valid", regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@" +
            "[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$")
    @Size(max = 128, message = "length <= 128")
    private String email;
}

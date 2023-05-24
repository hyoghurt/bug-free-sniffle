package org.example.tracker.dto.project;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReq {
    @NotBlank(message = "code required")
    private String code;
    @NotBlank(message = "name required")
    private String name;
    private String description;
}

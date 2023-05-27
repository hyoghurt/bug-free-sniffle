package org.example.tracker.dto.project;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReq {

    @Schema(description = "Код проекта - некоторое уникальное имя проекта. " +
            "Является обязательным и уникальным среди всех проектов.",
            example = "dammit-332")
    @NotBlank(message = "code required")
    @Size(max = 128, message = "1 <= code.length <= 128")
    private String code;

    @Schema(description = "Наименование - текстовое значение, содержащее короткое наименование проекта.",
            example = "паника")
    @NotBlank(message = "name required")
    @Size(max = 128, message = "1 <= name.length <= 128")
    private String name;

    @Schema(description = "Описание - текстовое значение содержащее более детальную информацию о проекте.",
            example = "хороший проект")
    @Size(max = 1024, message = "1 <= description.length <= 1024")
    private String description;
}

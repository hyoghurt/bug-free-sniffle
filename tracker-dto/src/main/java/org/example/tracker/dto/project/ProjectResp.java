package org.example.tracker.dto.project;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResp extends ProjectReq {

    @Schema(description = "уникальный идентификатор", example = "82")
    private int id;

    private ProjectStatus status;
}

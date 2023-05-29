package org.example.tracker.dto.project;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResp extends ProjectReq {
    private int id;
    private ProjectStatus status;
}

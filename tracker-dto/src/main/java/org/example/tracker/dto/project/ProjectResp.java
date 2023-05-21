package org.example.tracker.dto.project;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class ProjectResp extends ProjectReq {
    private int id;
    private ProjectStatus status;
}

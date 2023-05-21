package org.example.tracker.dto.project;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectUpdateStatusReq {
    private ProjectStatus status;
}

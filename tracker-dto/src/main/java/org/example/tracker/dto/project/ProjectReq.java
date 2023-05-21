package org.example.tracker.dto.project;


import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ProjectReq {
    private String code; //required
    private String name; //required
    private String description;
}

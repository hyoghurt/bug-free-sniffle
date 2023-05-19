package org.example.tracker.dao;

import lombok.Builder;
import lombok.Data;
import org.example.tracker.dto.project.ProjectStatus;

@Data
@Builder
public class SearchFilter {
    private String firstName;
    private String lastName;
    private String projectName;
    private ProjectStatus projectStatus;
}

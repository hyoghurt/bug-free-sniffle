package org.example.tracker.dto.project;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectFilterParam {
    private String query;
    private List<ProjectStatus> statuses;
}

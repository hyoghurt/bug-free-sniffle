package org.example.tracker.dao.entity;

import org.example.tracker.dao.enums.ProjectStatus;

public class ProjectEntity {
    private String code; //fk
    private String name; //required
    private String description;
    private ProjectStatus status; //required
}

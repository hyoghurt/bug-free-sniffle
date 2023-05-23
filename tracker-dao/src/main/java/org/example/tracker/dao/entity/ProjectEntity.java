package org.example.tracker.dao.entity;


import org.example.tracker.dto.project.ProjectStatus;

public class ProjectEntity {
    private int id; //fk
    private String code; //required
    private String name; //required
    private String description;
    private ProjectStatus status; //required
}

package org.example.tracker.dao.entity;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.example.tracker.dto.project.ProjectStatus;

import javax.annotation.processing.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProjectEntity.class)
public abstract class ProjectEntity_ {
    public static volatile SingularAttribute<ProjectEntity, Integer> id;
    public static volatile SingularAttribute<ProjectEntity, String> code;
    public static volatile SingularAttribute<ProjectEntity, String> name;
    public static volatile SingularAttribute<ProjectEntity, String> description;
    public static volatile SingularAttribute<ProjectEntity, ProjectStatus> status;
    public static volatile SetAttribute<ProjectEntity, TeamEmbeddable> teams;
}

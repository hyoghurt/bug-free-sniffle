package org.example.tracker.entity;


import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.example.tracker.dto.task.TaskStatus;

import javax.annotation.processing.Generated;
import java.time.Instant;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TaskEntity.class)
public abstract class TaskEntity_ {
    public static volatile SingularAttribute<TaskEntity, Integer> id;
    public static volatile SingularAttribute<TaskEntity, ProjectEntity> project;
    public static volatile SingularAttribute<TaskEntity, String> title;
    public static volatile SingularAttribute<TaskEntity, String> description;
    public static volatile SingularAttribute<TaskEntity, EmployeeEntity> assignees;
    public static volatile SingularAttribute<TaskEntity, Integer> authorId;
    public static volatile SingularAttribute<TaskEntity, TaskStatus> status;
    public static volatile SingularAttribute<TaskEntity, Integer> laborCostsInHours;
    public static volatile SingularAttribute<TaskEntity, Instant> createdDatetime;
    public static volatile SingularAttribute<TaskEntity, Instant> updateDatetime;
    public static volatile SingularAttribute<TaskEntity, Instant> deadlineDatetime;
}

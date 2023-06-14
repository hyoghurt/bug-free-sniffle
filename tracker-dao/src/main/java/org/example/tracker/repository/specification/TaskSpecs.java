package org.example.tracker.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import org.example.tracker.entity.EmployeeEntity;
import org.example.tracker.entity.EmployeeEntity_;
import org.example.tracker.entity.TaskEntity;
import org.example.tracker.entity.TaskEntity_;
import org.example.tracker.dto.task.TaskFilterParam;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class TaskSpecs {

    public static Specification<TaskEntity> byFilterParam(final TaskFilterParam param) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // title
            if (param.getQuery() != null && !param.getQuery().isBlank()) {
                String reg = "%" + param.getQuery().toUpperCase() + "%";
                predicates.add(cb.like(cb.upper(root.get(TaskEntity_.title)), reg));
            }

            // status
            if (param.getStatuses() != null && !param.getStatuses().isEmpty()) {
                predicates.add(root.get(TaskEntity_.status).in(param.getStatuses()));
            }

            // assignees
            if (param.getAssigneesId() != null) {
                Join<TaskEntity, EmployeeEntity> join = root.join(TaskEntity_.assignees);
                predicates.add(cb.equal(join.get(EmployeeEntity_.id), param.getAssigneesId()));
            }

            // author
            if (param.getAuthorId() != null) {
                predicates.add(cb.equal(root.get(TaskEntity_.authorId), param.getAuthorId()));
            }

            // created datetime
            Instant min = param.getMinCreatedDatetime();
            Instant max = param.getMaxCreatedDatetime();
            datetimePeriod(root, cb, predicates, min, max, TaskEntity_.createdDatetime);

            // deadline datetime
            min = param.getMinDeadlineDatetime();
            max = param.getMaxDeadlineDatetime();
            datetimePeriod(root, cb, predicates, min, max, TaskEntity_.deadlineDatetime);

            // order by created datetime
            query.orderBy(cb.asc(root.get(TaskEntity_.createdDatetime)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void datetimePeriod(Root<TaskEntity> root, CriteriaBuilder cb,
                                       List<Predicate> predicates,
                                       Instant min, Instant max,
                                       SingularAttribute<TaskEntity, Instant> datetime) {

        if (min != null && max != null) {
            predicates.add(cb.between(root.get(datetime), min, max));
        } else if (min != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(datetime), min));
        } else if (max != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(datetime), max));
        }
    }
}

package org.example.tracker.dao.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.EmployeeEntity_;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.entity.TaskEntity_;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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
            if (min != null && max != null) {
                predicates.add(cb.between(root.get(TaskEntity_.createdDatetime), min, max));
            } else if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(TaskEntity_.createdDatetime), min));
            } else if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(TaskEntity_.createdDatetime), max));
            }

            // deadline datetime
            min = param.getMinDeadlineDatetime();
            max = param.getMaxDeadlineDatetime();
            if (min != null && max != null) {
                predicates.add(cb.between(root.get(TaskEntity_.deadlineDatetime), min, max));
            } else if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(TaskEntity_.deadlineDatetime), min));
            } else if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(TaskEntity_.deadlineDatetime), max));
            }

            // order by created datetime
            query.orderBy(cb.asc(root.get(TaskEntity_.createdDatetime)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<TaskEntity> byTitleLikeIgnoreCase(final String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            return cb.like(cb.upper(root.get(TaskEntity_.title)), "%" + search.toUpperCase() + "%");
        };
    }

    public static Specification<TaskEntity> byAssigneesIdEquals(final Integer id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            Join<TaskEntity, EmployeeEntity> join = root.join(TaskEntity_.assignees);
            return cb.equal(join.get(EmployeeEntity_.id), id);
        };
    }

    public static Specification<TaskEntity> byAuthorIdEquals(final Integer id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get(TaskEntity_.authorId), id);
        };
    }

    public static Specification<TaskEntity> createdBetween(final Instant min, final Instant max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get(TaskEntity_.createdDatetime), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get(TaskEntity_.createdDatetime), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get(TaskEntity_.createdDatetime), max);
            }
            return null;
        };
    }

    public static Specification<TaskEntity> deadlineBetween(final Instant min, final Instant max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get(TaskEntity_.deadlineDatetime), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get(TaskEntity_.deadlineDatetime), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get(TaskEntity_.deadlineDatetime), max);
            }
            return null;
        };
    }

    public static Specification<TaskEntity> byStatusIn(final Collection<TaskStatus> collection) {
        return (root, query, cb) -> {
            if (collection == null || collection.isEmpty()) return null;
            return root.get(TaskEntity_.status).in(collection);
        };
    }

    public static Specification<TaskEntity> orderByCreatedDatetime(Specification<TaskEntity> spec) {
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get(TaskEntity_.createdDatetime)));
            return spec.toPredicate(root, query, cb);
        };
    }
}

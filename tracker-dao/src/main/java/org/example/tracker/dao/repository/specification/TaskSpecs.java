package org.example.tracker.dao.repository.specification;

import jakarta.persistence.criteria.Join;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.EmployeeEntity_;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.entity.TaskEntity_;
import org.example.tracker.dto.task.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Collection;

public final class TaskSpecs {
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

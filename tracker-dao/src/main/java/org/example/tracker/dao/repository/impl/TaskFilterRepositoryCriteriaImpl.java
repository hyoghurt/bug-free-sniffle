package org.example.tracker.dao.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.TaskFilterRepository;
import org.example.tracker.dto.task.TaskFilterParam;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskFilterRepositoryCriteriaImpl implements TaskFilterRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<TaskEntity> findByFilter(TaskFilterParam filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TaskEntity> criteriaQuery = builder.createQuery(TaskEntity.class);
        Root<TaskEntity> root = criteriaQuery.from(TaskEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        findLikeIgnoreCasePredicate("title", filter.getQuery(), builder, root)
                .ifPresent(predicates::add);

        findInPredicate("status", filter.getStatuses(), root)
                .ifPresent(predicates::add);

        findEqualPredicate("assignees_id", filter.getAssigneesId(), builder, root)
                .ifPresent(predicates::add);

        findEqualPredicate("author_id", filter.getAuthorId(), builder, root)
                .ifPresent(predicates::add);

        findBetweenPredicate(
                "created_datetime", filter.getMinCreatedDatetime(), filter.getMaxCreatedDatetime(),
                builder, root).ifPresent(predicates::add);

        findBetweenPredicate(
                "deadline_datetime", filter.getMinDeadlineDatetime(), filter.getMaxDeadlineDatetime(),
                builder, root).ifPresent(predicates::add);

        criteriaQuery
                .select(root)
                .where(builder.and(predicates.toArray(new Predicate[0])))
                .orderBy(builder.asc(root.get("created_datetime")));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private static Optional<Predicate> findBetweenPredicate(String field, Instant min, Instant max,
                                                            CriteriaBuilder builder, Root<TaskEntity> root) {
        if (min != null && max != null) {
            Predicate predicate = builder.between(root.get(field), min, max);
            return Optional.of(predicate);
        } else if (min != null) {
            Predicate predicate = builder.greaterThanOrEqualTo(root.get(field), min);
            return Optional.of(predicate);
        } else if (max != null) {
            Predicate predicate = builder.lessThanOrEqualTo(root.get(field), max);
            return Optional.of(predicate);
        }
        return Optional.empty();
    }

    private static Optional<Predicate> findInPredicate(String field, Collection<?> collection,
                                                       Root<TaskEntity> root) {
        if (collection != null && !collection.isEmpty()) {
            Predicate predicate = root.get(field).in(collection);
            return Optional.of(predicate);
        }
        return Optional.empty();
    }

    private static Optional<Predicate> findEqualPredicate(String field, Object id,
                                                          CriteriaBuilder builder, Root<TaskEntity> root) {
        if (id != null) {
            Predicate predicate = builder.equal(root.get(field), id);
            return Optional.of(predicate);
        }
        return Optional.empty();
    }

    private static Optional<Predicate> findLikeIgnoreCasePredicate(String field, String value,
                                                                   CriteriaBuilder builder, Root<TaskEntity> root) {
        if (value != null && !value.isBlank()) {
            Predicate predicate = builder.like(builder.upper(root.get(field)), "%" + value.toUpperCase() + "%");
            return Optional.of(predicate);
        }
        return Optional.empty();
    }
}

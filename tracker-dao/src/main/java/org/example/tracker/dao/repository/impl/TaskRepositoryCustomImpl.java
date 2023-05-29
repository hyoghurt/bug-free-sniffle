package org.example.tracker.dao.repository.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.TaskRepositoryCustom;
import org.example.tracker.dto.task.TaskFilterParam;

import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryCustomImpl extends BaseCriteriaRepository implements TaskRepositoryCustom {

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
}

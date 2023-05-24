package org.example.tracker.dao.repository.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.repository.ProjectRepositoryCustom;
import org.example.tracker.dto.project.ProjectFilterParam;

import java.util.ArrayList;
import java.util.List;

public class ProjectRepositoryCustomImpl extends BaseCriteriaRepository implements ProjectRepositoryCustom {

    @Override
    public List<ProjectEntity> findAllByFilter(ProjectFilterParam filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> criteriaQuery = builder.createQuery(ProjectEntity.class);
        Root<ProjectEntity> root = criteriaQuery.from(ProjectEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        findInPredicate("status", filter.getStatuses(), root)
                .ifPresent(predicates::add);

        findOrLikeIgnoreCasePredicate(List.of("name", "code"), filter.getQuery(), builder, root)
                .ifPresent(predicates::add);

        criteriaQuery
                .select(root)
                .where(builder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}

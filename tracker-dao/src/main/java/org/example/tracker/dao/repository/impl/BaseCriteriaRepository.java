package org.example.tracker.dao.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public abstract class BaseCriteriaRepository {

    @PersistenceContext
    EntityManager entityManager;

    static <T> Optional<Predicate> findBetweenPredicate(String field, Instant min, Instant max,
                                                        CriteriaBuilder builder, Root<T> root) {
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

    static <T> Optional<Predicate> findInPredicate(String field, Collection<?> collection,
                                                   Root<T> root) {
        if (collection != null && !collection.isEmpty()) {
            Predicate predicate = root.get(field).in(collection);
            return Optional.of(predicate);
        }
        return Optional.empty();
    }

    static <T> Optional<Predicate> findEqualPredicate(String field, Object id,
                                                      CriteriaBuilder builder, Root<T> root) {
        if (id != null) {
            Predicate predicate = builder.equal(root.get(field), id);
            return Optional.of(predicate);
        }
        return Optional.empty();
    }

    static <T> Optional<Predicate> findLikeIgnoreCasePredicate(String field, String value,
                                                               CriteriaBuilder builder, Root<T> root) {
        if (value != null && !value.isBlank()) {
            Predicate predicate = builder.like(
                    builder.upper(root.get(field)), "%" + value.toUpperCase() + "%");
            return Optional.of(predicate);
        }
        return Optional.empty();
    }
}

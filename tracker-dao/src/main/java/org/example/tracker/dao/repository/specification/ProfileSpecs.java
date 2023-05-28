package org.example.tracker.dao.repository.specification;

import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.ProjectEntity_;
import org.example.tracker.dto.project.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class ProfileSpecs {
    public static Specification<ProjectEntity> byCodeLikeIgnoreCase(final String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            return cb.like(cb.upper(root.get(ProjectEntity_.code)), "%" + search.toUpperCase() + "%");
        };
    }

    public static Specification<ProjectEntity> byNameLikeIgnoreCase(final String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            return cb.like(cb.upper(root.get(ProjectEntity_.name)), "%" + search.toUpperCase() + "%");
        };
    }

    public static Specification<ProjectEntity> byStatusIn(final Collection<ProjectStatus> collection) {
        return (root, query, cb) -> {
            if (collection == null || collection.isEmpty()) return null;
            return root.get(ProjectEntity_.status).in(collection);
        };
    }
}

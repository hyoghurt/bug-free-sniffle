package org.example.tracker.dao.datastorage;

import java.util.List;

public interface Table<T, R> {

    T create(T entity);

    T update(T entity);

    List<T> getAll();

    T getById(R id);

    void deleteById(R id);
}

package org.example.tracker.dao.datastorage;

import org.example.tracker.dao.datastorage.impl.TableImpl;

import java.util.List;

public interface DataStorage {
    <T, R> void addTable(TableImpl<T, R> table, Class<?> entityClass);

    <T> T create(T entity);

    <T> T update(T entity);

    <T> List<T> getAll(Class<T> entityClass);

    <T, R> T getById(R id, Class<T> entityClass);

    <T, R> void deleteById(R id, Class<T> entityClass);
}

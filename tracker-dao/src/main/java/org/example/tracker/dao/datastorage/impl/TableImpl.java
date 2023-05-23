package org.example.tracker.dao.datastorage.impl;

import org.example.tracker.dao.datastorage.Table;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableImpl<T, R> implements Table<T, R>, Serializable {
    @Serial
    private static final long serialVersionUID = 42L;
    private final Map<R, T> map = new ConcurrentHashMap<>();
    private final String foreignKey;

    public TableImpl(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create(T entity) {
        try {
            if (foreignKey == null) throw new RuntimeException("foreign key not found");
            Field field = entity.getClass().getDeclaredField(foreignKey);
            field.setAccessible(true);
            R id = (R) field.get(entity);
            T result = map.putIfAbsent(id, entity);
            if (result != null) throw new RuntimeException("foreign key already exists");
            return entity;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T update(T entity) {
        try {
            if (foreignKey == null) throw new RuntimeException("foreign key not found");
            Field field = entity.getClass().getDeclaredField(foreignKey);
            field.setAccessible(true);
            R id = (R) field.get(entity);
            T replace = map.replace(id, entity);
            if (replace == null) throw new RuntimeException("not found foreign key: " + id);
            return replace;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public T getById(R id) {
        T result = map.get(id);
        if (result == null) throw new RuntimeException("not found foreign key: " + id);
        return result;
    }

    @Override
    public void deleteById(R id) {
        if (map.remove(id) == null) throw new RuntimeException("not found foreign key: " + id);
    }
}

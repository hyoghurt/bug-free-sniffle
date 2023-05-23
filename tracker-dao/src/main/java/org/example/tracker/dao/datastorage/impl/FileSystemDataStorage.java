package org.example.tracker.dao.datastorage.impl;

import org.example.tracker.dao.datastorage.DataStorage;
import org.example.tracker.dao.datastorage.Table;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * хранит объект Table в словаре и сериализует словарь в файл.
 * данный класс является Singleton.
 * при создании десериализует данные с файла, если файл существует.
 */
public class FileSystemDataStorage implements DataStorage {
    private Map<Class<?>, Table<?, ?>> tables;
    private static volatile FileSystemDataStorage instance;
    private final String FILE_NAME = "customdata";

    @SuppressWarnings("unchecked")
    private FileSystemDataStorage() {
        // инициализируем данные с файла, если файл существует
        try (FileInputStream fileInputStream = new FileInputStream(FILE_NAME);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            tables = (Map<Class<?>, Table<?, ?>>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            tables = new ConcurrentHashMap<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileSystemDataStorage getInstance() {
        if (instance == null) {
            synchronized (FileSystemDataStorage.class) {
                if (instance == null) {
                    instance = new FileSystemDataStorage();
                }
            }
        }
        return instance;
    }

    @Override
    public <T, R> void addTable(TableImpl<T, R> table, Class<?> entityClass) {
        Table<?, ?> result = tables.putIfAbsent(entityClass, table);
        if (result != null) {
            System.out.println("table already exists " + entityClass.getName());
        } else {
            System.out.println("create table " + entityClass.getName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(T entity) {
        Table<T, ?> table = (TableImpl<T, ?>) tables.get(entity.getClass());
        T result = table.create(entity);
        writeObjectToFile();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T update(T entity) {
        TableImpl<T, ?> table = (TableImpl<T, ?>) tables.get(entity.getClass());
        T result = table.update(entity);
        writeObjectToFile();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> T getById(R id, Class<T> entityClass) {
        TableImpl<T, R> table = (TableImpl<T, R>) tables.get(entityClass);
        return table.getById(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> entityClass) {
        TableImpl<T, ?> table = (TableImpl<T, ?>) tables.get(entityClass);
        return table.getAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> void deleteById(R id, Class<T> entityClass) {
        TableImpl<T, R> table = (TableImpl<T, R>) tables.get(entityClass);
        table.deleteById(id);
        writeObjectToFile();
    }

    private void writeObjectToFile() {
        // записываем данные в файл
        try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(tables);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

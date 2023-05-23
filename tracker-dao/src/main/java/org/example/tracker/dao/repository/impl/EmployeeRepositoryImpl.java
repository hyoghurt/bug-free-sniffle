package org.example.tracker.dao.repository.impl;

import org.example.tracker.dao.datastorage.DataStorage;
import org.example.tracker.dao.datastorage.impl.FileSystemDataStorage;
import org.example.tracker.dao.datastorage.impl.TableImpl;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dto.employee.EmployeeStatus;

import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final DataStorage dataStorage;

    public EmployeeRepositoryImpl() {
        dataStorage = FileSystemDataStorage.getInstance();
        dataStorage.addTable(new TableImpl<EmployeeEntity, Integer>("id"), EmployeeEntity.class);
    }

    @Override
    public EmployeeEntity create(EmployeeEntity entity) {
        return dataStorage.create(entity);
    }

    @Override
    public EmployeeEntity update(EmployeeEntity entity) {
        return dataStorage.update(entity);
    }

    @Override
    public EmployeeEntity getById(Integer id) {
        return dataStorage.getById(id, EmployeeEntity.class);
    }

    @Override
    public List<EmployeeEntity> getAll() {
        return dataStorage.getAll(EmployeeEntity.class);
    }

    /**
     * изменяет статус на DELETED
     */
    @Override
    public void deleteById(Integer id) {
        EmployeeEntity entity = getById(id);
        entity.setStatus(EmployeeStatus.DELETED);
        dataStorage.update(entity);
    }
}

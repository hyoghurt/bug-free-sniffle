package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.exception.EmployeeIsDeletedException;
import org.example.tracker.service.exception.EmployeeNotFoundException;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    public void create(EmployeeReq request) {
        EmployeeEntity entity = modelMapper.toEmployeeEntity(request);
        employeeRepository.save(entity);
    }

    @Override
    public EmployeeResp getById(Integer id) {
        EmployeeEntity entity = getEmployeeEntity(id);
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public EmployeeResp getByUpn(String upn) {
        EmployeeEntity entity = employeeRepository.findByUpnIgnoreCase(upn);
        if (entity == null) {
            throw new EmployeeNotFoundException("not found " + upn);
        }
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public void update(Integer id, EmployeeReq request) {
        EmployeeEntity entity = modelMapper.toEmployeeEntity(request);
        if (entity.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new EmployeeIsDeletedException("employee is deleted: " + entity.getId());
        }
        entity.setId(id);
        employeeRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        EmployeeEntity entity = getEmployeeEntity(id);
        entity.setStatus(EmployeeStatus.DELETED);
    }

    @Override
    public List<EmployeeResp> find(String query) {
        List<EmployeeEntity> entities = employeeRepository.findByNameEmailUpnActiveStatus(query);
        return entities.stream()
                .map(modelMapper::toEmployeeResp)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeEntity getEmployeeEntity(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("not found " + id));
    }
}

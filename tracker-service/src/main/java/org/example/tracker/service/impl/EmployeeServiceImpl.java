package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.service.exception.DuplicateUniqueFieldException;
import org.example.tracker.service.exception.EmployeeNotFoundException;
import org.example.tracker.service.exception.RequiredFieldException;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
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
    public EmployeeResp getById(Integer id) {
        EmployeeEntity entity = getEmployeeEntity(id);
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public EmployeeResp getByUpn(String upn) {
        EmployeeEntity entity = employeeRepository.findByUpnIgnoreCase(upn);
        if (entity == null) throw new EmployeeNotFoundException("not found " + upn);
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public EmployeeResp create(EmployeeReq request) {
        EmployeeEntity entity = modelMapper.toEmployeeEntity(request);
        save(entity);
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public EmployeeResp update(Integer id, EmployeeReq request) {
        EmployeeEntity entity = getEmployeeEntity(id);
        if (entity.getStatus().equals(EmployeeStatus.DELETED)) {
            throw new EmployeeAlreadyDeletedException("employee already deleted: " + entity.getId());
        }
        mergeRequestToEntity(request, entity);
        save(entity);
        return modelMapper.toEmployeeResp(entity);
    }

    private void save(EmployeeEntity entity) {
        try {
            employeeRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("employees_upn_key")) {
                throw new DuplicateUniqueFieldException("employee duplicate upn " + entity.getUpn());
            } else if (e.getMessage().contains("null value in column")) {
                throw new RequiredFieldException();
            }
            throw e;
        }
    }

    private void mergeRequestToEntity(EmployeeReq request, EmployeeEntity entity) {
        entity.setUpn(request.getUpn());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setMiddleName(request.getMiddleName());
        entity.setEmail(request.getEmail());
        entity.setPosition(request.getPosition());
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

    @Override
    public boolean isDeleted(EmployeeEntity entity) {
        return entity.getStatus().equals(EmployeeStatus.DELETED);
    }
}

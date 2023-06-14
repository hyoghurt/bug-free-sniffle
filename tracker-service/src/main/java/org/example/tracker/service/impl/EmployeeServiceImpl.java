package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.dto.employee.EmployeeFilterParam;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.entity.EmployeeEntity;
import org.example.tracker.exception.DuplicateUniqueFieldException;
import org.example.tracker.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.exception.EmployeeNotFoundException;
import org.example.tracker.mapper.ModelMapper;
import org.example.tracker.repository.EmployeeRepository;
import org.example.tracker.repository.specification.EmployeeSpecs;
import org.example.tracker.service.EmployeeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResp getById(Integer id) {
        log.info("get employee {}", id);
        EmployeeEntity entity = getEmployeeEntity(id);
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public EmployeeResp create(EmployeeReq request) {
        log.info("create: {}", request);
        EmployeeEntity entity = new EmployeeEntity();
        mergeRequestToEntity(request, entity);
        save(entity);
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public EmployeeResp update(Integer id, EmployeeReq request) {
        log.info("update id: {} body: {}", id, request);
        EmployeeEntity entity = getEmployeeEntity(id);
        checkIsDeleted(entity);
        mergeRequestToEntity(request, entity);
        save(entity);
        return modelMapper.toEmployeeResp(entity);
    }

    @Override
    public EmployeeEntity getEmployeeEntity(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("not found " + id));
    }

    private void checkIsDeleted(EmployeeEntity entity) {
        if (isDeleted(entity)) {
            log.info("employee {} is deleted", entity.getId());
            throw new EmployeeAlreadyDeletedException("employee already deleted: " + entity.getId());
        }
    }

    @Override
    public boolean isDeleted(EmployeeEntity entity) {
        return entity.getStatus().equals(EmployeeStatus.DELETED);
    }

    private void mergeRequestToEntity(EmployeeReq request, EmployeeEntity entity) {
        entity.setUpn(request.getUpn());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setMiddleName(request.getMiddleName());
        entity.setEmail(request.getEmail());
        entity.setPosition(request.getPosition());
    }

    private void save(EmployeeEntity entity) {
        try {
            employeeRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("employees_upn_key")) {
                log.info("duplicate upn: {}", entity.getUpn());
                throw new DuplicateUniqueFieldException("employee duplicate upn " + entity.getUpn());
            }
            throw e;
        }
    }

    @Override
    public void delete(Integer id) {
        log.info("delete id: {}", id);
        int result = employeeRepository.updateSetStatusDeletedById(id);
        if (result == 0) {
            throw new EmployeeNotFoundException("not found employee " + id);
        }
    }

    @Override
    public List<EmployeeResp> getAllByParam(EmployeeFilterParam param) {
        log.info("get all by param: {}", param);
        return employeeRepository.findAll(EmployeeSpecs.byFilterParam(param)).stream()
                .map(modelMapper::toEmployeeResp)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeEntity getEmployeeEntityByUpn(String upn) {
        return employeeRepository.findByUpn(upn)
                .orElseThrow(() -> new EmployeeNotFoundException("not found upn " + upn));
    }
}

package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskUpdateStatusReq;
import org.example.tracker.service.TaskService;
import org.example.tracker.service.exception.TaskNotFoundException;
import org.example.tracker.service.exception.TaskStatusIncorrectFlowUpdateException;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;

    @Override
    public void create(TaskReq request) {
        TaskEntity entity = modelMapper.toTaskEntity(request);
        //TODO get author id
        entity.setAuthorId(1);
        //TODO exception db
        taskRepository.save(entity);
    }

    @Override
    public void update(Integer id, TaskReq request) {
        TaskEntity entity = getTaskEntity(id);
        //TODO get author id
        entity.setAuthorId(1);
        entity.setUpdateDatetime(Instant.now());
        //TODO update employee other
        taskRepository.save(entity);
    }

    @Override
    public List<TaskResp> findByParam(TaskFilterParam param) {
        //TODO create filter
        return null;
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, TaskUpdateStatusReq request) {
        TaskEntity entity = getTaskEntity(id);
        if (request.getStatus().ordinal() < entity.getStatus().ordinal()) {
            throw new TaskStatusIncorrectFlowUpdateException(String
                    .format("task status incorrect flow update: %s -> %s",
                            entity.getStatus().name(),
                            request.getStatus().name()));
        }
        entity.setStatus(request.getStatus());
    }

    private TaskEntity getTaskEntity(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("task not found " + id));
    }
}
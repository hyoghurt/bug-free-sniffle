package org.example.tracker.service;

import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskUpdateStatusReq;

import java.util.List;

public interface TaskService {
    void create(TaskReq request);
    void update(Integer id, TaskReq request);
    List<TaskResp> findByParam(TaskFilterParam param);
    void updateStatus(Integer id, TaskUpdateStatusReq request);
}

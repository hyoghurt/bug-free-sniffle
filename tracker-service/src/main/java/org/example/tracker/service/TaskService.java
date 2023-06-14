package org.example.tracker.service;

import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskUpdateStatusReq;

import java.util.List;

public interface TaskService {
    TaskResp create(TaskReq request);
    TaskResp update(Integer id, TaskReq request);
    List<TaskResp> getAllByParam(TaskFilterParam param);
    void updateStatus(Integer id, TaskUpdateStatusReq request);
}

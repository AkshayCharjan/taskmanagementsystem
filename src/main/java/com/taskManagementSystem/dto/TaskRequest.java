package com.taskManagementSystem.dto;

import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TaskRequest {
    private Status status;
    private Priority priority;
    private UUID assignedTo;
    private UUID projectId;
}

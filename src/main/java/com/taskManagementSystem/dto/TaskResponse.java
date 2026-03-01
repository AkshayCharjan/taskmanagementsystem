package com.taskManagementSystem.dto;

import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TaskResponse {
    private UUID id;
    private String title;
    private Status status;
    private Priority priority;
    private UserResponse assignedTo;
    private UUID projectId;
    private String projectName;

}

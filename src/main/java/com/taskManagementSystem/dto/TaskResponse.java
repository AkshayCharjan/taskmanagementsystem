package com.taskManagementSystem.dto;

import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;

import java.util.UUID;

public class TaskResponse {
    private UUID id;
    private Status status;
    private Priority priority;
    private UserResponse assignedTo;

}

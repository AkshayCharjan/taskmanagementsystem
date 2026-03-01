package com.taskManagementSystem.dto;

import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;
    private Status status;
    private Priority priority;
    private UUID assignedTo;
    private UUID projectId;
}

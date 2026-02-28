package com.taskManagementSystem.entity;

import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private Status status;
    private Priority priority;
    @ManyToOne
    private User assignedTo;
}

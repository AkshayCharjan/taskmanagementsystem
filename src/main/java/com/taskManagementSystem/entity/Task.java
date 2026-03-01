package com.taskManagementSystem.entity;

import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Entity
@Setter
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}

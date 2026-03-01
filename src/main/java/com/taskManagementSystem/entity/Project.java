package com.taskManagementSystem.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}

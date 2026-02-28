package com.taskManagementSystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
@Setter
public class User {
    @Id
    private UUID id;
    private int name;
    private String email;
    private String password;
}

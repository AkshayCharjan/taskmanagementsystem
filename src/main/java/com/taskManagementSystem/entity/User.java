package com.taskManagementSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taskManagementSystem.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Entity
@Setter
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    private String name;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;  // Default role
}

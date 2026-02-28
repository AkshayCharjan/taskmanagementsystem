package com.taskManagementSystem.repository;

import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}

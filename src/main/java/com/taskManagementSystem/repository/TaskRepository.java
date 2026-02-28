package com.taskManagementSystem.repository;

import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByStatusAndPriority(Status status, Priority priority);
    List<Task> findByStatus(Status status);
    List<Task> findByPriority(Priority priority);
}

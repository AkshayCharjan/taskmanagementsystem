package com.taskManagementSystem.repository;

import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByStatusAndPriority(Status status, Priority priority);

    Page<Task> findByStatusAndPriority(Status status, Priority priority, Pageable pageable);

    List<Task> findByStatus(Status status);

    Page<Task> findByStatus(Status status, Pageable pageable);

    List<Task> findByPriority(Priority priority);

    Page<Task> findByPriority(Priority priority, Pageable pageable);

    Page<Task> findByAssignedTo(User user, Pageable pageable);
}

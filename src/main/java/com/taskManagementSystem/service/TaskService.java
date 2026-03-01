package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.TaskRequest;
import com.taskManagementSystem.dto.TaskResponse;
import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.entity.Project;
import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.ProjectRepository;
import com.taskManagementSystem.repository.TaskRepository;
import com.taskManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse createTask(TaskRequest taskRequest){
        Task task = new Task();
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        if(taskRequest.getAssignedTo() != null) {
            User user = userRepository.findById(taskRequest.getAssignedTo())
                    .orElseThrow(() -> {
                        log.error("User not found id={}", taskRequest.getAssignedTo());
                        return new ResourceNotFoundException("User not found");
                    });
            task.setAssignedTo(user);
        }

        if(taskRequest.getProjectId() != null) {
            Project project = projectRepository.findById(taskRequest.getProjectId())
                    .orElseThrow(() -> {
                        log.error("Project not found id={}", taskRequest.getProjectId());
                        return new ResourceNotFoundException("Project not found");
                    });
            task.setProject(project);
        }

        taskRepository.save(task);
        log.info("Task created successfully id={}", task.getId());
        return toTaskResponse(task);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse updateTask(TaskRequest updatedTask, UUID taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found id={}", taskId);
                    return new ResourceNotFoundException("Task not found");
                });

        if(updatedTask.getPriority() != null)
            task.setPriority(updatedTask.getPriority());

        if(updatedTask.getStatus() != null) {
            validateStatusTransition(task.getStatus(), updatedTask.getStatus());
            task.setStatus(updatedTask.getStatus());
        }

        if(updatedTask.getAssignedTo() != null){
            User user = userRepository.findById(updatedTask.getAssignedTo())
                        .orElseThrow(() -> {
                            log.error("User not found id={}", updatedTask.getAssignedTo());
                            return new ResourceNotFoundException("User not found");
                        });
            task.setAssignedTo(user);
        }

        if(updatedTask.getProjectId() != null){
            Project project = projectRepository.findById(updatedTask.getProjectId())
                    .orElseThrow(() -> {
                        log.error("Project not found id={}", updatedTask.getProjectId());
                        return new ResourceNotFoundException("Project not found");
                    });
            task.setProject(project);
        }

        taskRepository.save(task);
        log.info("Task updated successfully id={}", taskId);
        return toTaskResponse(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(UUID taskId){
        taskRepository.deleteById(taskId);
        log.info("Task deleted successfully id={}", taskId);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Page<TaskResponse> getFilteredTasks(Status status, Priority priority, Pageable pageable){
        Page<Task> tasks;
        if(status != null && priority != null){
            tasks =  taskRepository.findByStatusAndPriority(status, priority, pageable);
        }
        else if(status != null) {
            tasks =  taskRepository.findByStatus(status, pageable);
        }
        else if(priority != null) {
            tasks =  taskRepository.findByPriority(priority, pageable);
        }
        else
            tasks =  taskRepository.findAll(pageable);
        return tasks.map(this::toTaskResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse assignTask(UUID taskId, UUID userID){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found id={}", taskId);
                    return new ResourceNotFoundException("Task not found");
                });
        User user = userRepository.findById(userID)
                        .orElseThrow(() -> {
                            log.error("User not found id={}", userID);
                            return new ResourceNotFoundException("User not found");
                        });
        task.setAssignedTo(user);
        taskRepository.save(task);
        log.info("Task assigned successfully id={}, userId={}", taskId, userID);
        return toTaskResponse(task);
    }

    private void validateStatusTransition(Status current, Status next) {
        if (current == next) return;

        switch (current) {
            case OPEN:
                if (next != Status.IN_PROGRESS && next != Status.CLOSED) {
                    log.error("Invalid status transition: {} -> {}", current, next);
                    throw new IllegalStateException(
                        "OPEN can only transition to IN_PROGRESS or CLOSED"
                    );
                }
                break;

            case IN_PROGRESS:
                if (next != Status.CLOSED) {
                    log.error("Invalid status transition: {} -> {}", current, next);
                    throw new IllegalStateException(
                        "IN_PROGRESS can only transition to CLOSED"
                    );
                }
                break;

            case CLOSED:
                log.error("Invalid status transition: {} -> {}", current, next);
                throw new IllegalStateException(
                    "CLOSED tasks cannot be reopened"
                );
        }
    }

    private TaskResponse toTaskResponse(Task task){
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setPriority(task.getPriority());
        taskResponse.setStatus(task.getStatus());

        User assignedUser = task.getAssignedTo();
        UserResponse userResponse = new UserResponse();
        if(assignedUser != null){
            userResponse.setId(assignedUser.getId());
            userResponse.setName(assignedUser.getName());
            userResponse.setEmail(assignedUser.getEmail());
            userResponse.setRole(assignedUser.getRole());
        }
        taskResponse.setAssignedTo(userResponse);
        return taskResponse;
    }
}

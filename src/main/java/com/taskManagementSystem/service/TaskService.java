package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.TaskRequest;
import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.TaskRepository;
import com.taskManagementSystem.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(TaskRequest taskRequest){
        Task task = new Task();
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        if(taskRequest.getAssignedTo() != null) {
            User user = userRepository.findById(taskRequest.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedTo(user);
        }
        taskRepository.save(task);
        return task;
    }

    public Task updateTask(TaskRequest updatedTask, UUID taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if(updatedTask.getPriority() != null)
            task.setPriority(updatedTask.getPriority());

        if(updatedTask.getStatus() != null)
            task.setStatus(updatedTask.getStatus());

        if(updatedTask.getAssignedTo() != null){
            User user = userRepository.findById(updatedTask.getAssignedTo())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedTo(user);
        }
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(UUID taskId){
        taskRepository.deleteById(taskId);
    }

    public Page<Task> getFilteredTasks(Status status, Priority priority, Pageable pageable){
        if(status != null && priority != null){
            return taskRepository.findByStatusAndPriority(status, priority, pageable);
        }
        else if(status != null) {
            return taskRepository.findByStatus(status, pageable);
        }
        else if(priority != null) {
            return taskRepository.findByPriority(priority, pageable);
        }
        else return taskRepository.findAll(pageable);
    }

    public Task assignTask(UUID taskId, UUID userID){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User user = userRepository.findById(userID)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        task.setAssignedTo(user);
        taskRepository.save(task);
        return task;
    }
}

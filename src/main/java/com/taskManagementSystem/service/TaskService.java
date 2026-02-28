package com.taskManagementSystem.service;

import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.TaskRepository;
import com.taskManagementSystem.repository.UserRepository;
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

    public Task createTask(Task task){
        taskRepository.save(task);
        return task;
    }

    public Task updateTask(Task updatedTask, UUID taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));        if(updatedTask.getPriority() != null)
            task.setPriority(updatedTask.getPriority());
        if(updatedTask.getStatus() != null)
            task.setStatus(updatedTask.getStatus());
        if(updatedTask.getAssignedTo() != null)
            task.setAssignedTo(updatedTask.getAssignedTo());
        taskRepository.save(task);
        return task;
    }

    public void deleteTask(UUID taskId){
        taskRepository.deleteById(taskId);
    }

    public List<Task> getFilteredTasks(Status status, Priority priority){
        if(status != null && priority != null){
            return taskRepository.findByStatusAndPriority(status, priority);
        }
        else if(status != null) {
            return taskRepository.findByStatus(status);
        }
        else if(priority != null) {
            return taskRepository.findByPriority(priority);
        }
        else return taskRepository.findAll();
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

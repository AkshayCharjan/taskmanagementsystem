package com.taskManagementSystem.service;

import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
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

    public void createTask(Task task){
        taskRepository.save(task);
    }

    public void updateTask(Task updatedTask){
        Task task = taskRepository.findById(updatedTask.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setPriority(updatedTask.getPriority());
        task.setStatus(updatedTask.getStatus());
        task.setAssignedTo(updatedTask.getAssignedTo());
        taskRepository.save(task);
    }

    public void deleteTask(Task task){
        taskRepository.delete(task);
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

    public void assignTask(UUID taskId, UUID userID){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userID)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        task.setAssignedTo(user);
        taskRepository.save(task);
    }
}

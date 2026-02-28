package com.taskManagementSystem.controller;

import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import com.taskManagementSystem.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public void createTask(@RequestBody Task task){
        taskService.createTask(task);
    }

    @PutMapping("/{taskId}")
    public void updateTask(@RequestBody Task updatedtask, @PathVariable UUID taskId){
        taskService.updateTask(updatedtask, taskId);
    }

    @GetMapping
    public List<Task> getFilteredTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority){
        return taskService.getFilteredTasks(status, priority);
    }

    @DeleteMapping("{taskId}")
    public void deleteTask(@PathVariable UUID taskId){
        taskService.deleteTask(taskId);
    }

    @PatchMapping("/{taskId}/user/{userId}")
    public void assignTask(@PathVariable UUID taskId,
                           @PathVariable UUID userId){
        taskService.assignTask(taskId,userId);
    }

}

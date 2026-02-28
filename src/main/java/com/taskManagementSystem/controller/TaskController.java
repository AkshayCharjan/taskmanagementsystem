package com.taskManagementSystem.controller;

import com.taskManagementSystem.dto.TaskRequest;
import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Status;
import com.taskManagementSystem.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@RequestBody TaskRequest task){
        return taskService.createTask(task);
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@RequestBody TaskRequest updatedtask, @PathVariable UUID taskId){
        return taskService.updateTask(updatedtask, taskId);
    }

    @GetMapping
    public Page<Task> getFilteredTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @PageableDefault(size = 10, sort = "priority") Pageable pageable){
        return taskService.getFilteredTasks(status, priority, pageable);
    }

    @DeleteMapping("{taskId}")
    public void deleteTask(@PathVariable UUID taskId){
        taskService.deleteTask(taskId);
    }

    @PatchMapping("/{taskId}/user/{userId}")
    public Task assignTask(@PathVariable UUID taskId,
                           @PathVariable UUID userId){
        return taskService.assignTask(taskId,userId);
    }

}

package com.taskManagementSystem.controller;

import com.taskManagementSystem.dto.ProjectRequest;
import com.taskManagementSystem.dto.ProjectResponse;
import com.taskManagementSystem.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponse createProject(@RequestBody ProjectRequest request) {

        return projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectResponse> getAllProjects() {

        return projectService.getAllProjects();
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getProject(@PathVariable UUID projectId) {

        return projectService.getProject(projectId);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable UUID projectId) {

        projectService.deleteProject(projectId);
    }

}
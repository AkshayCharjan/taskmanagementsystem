package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.ProjectRequest;
import com.taskManagementSystem.dto.ProjectResponse;
import com.taskManagementSystem.dto.TaskResponse;
import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.entity.Project;
import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.exception.BadRequestException;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.ProjectRepository;
import com.taskManagementSystem.repository.TaskRepository;
import com.taskManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository,
                          TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse createProject(ProjectRequest request) {


        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.error("Project name is required");
            throw new BadRequestException("Project name is required");
        }


        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found email={}", email);
                    return new ResourceNotFoundException("User not found");
                });
        project.setCreatedBy(user);


        projectRepository.save(project);

        log.info("Project created successfully id={}", project.getId());

        return toProjectResponse(project);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::toProjectResponse)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project not found id={}", projectId);
                    return new ResourceNotFoundException("Project not found");
                });

        return toProjectResponse(project);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(UUID projectId) {

        if (!projectRepository.existsById(projectId)) {
            log.error("Project not found id={}", projectId);
            throw new ResourceNotFoundException("Project not found");
        }

        projectRepository.deleteById(projectId);

        log.info("Project deleted successfully id={}", projectId);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    private TaskResponse mapToTaskResponse(Task task){

        TaskResponse taskResponse = new TaskResponse();

        taskResponse.setId(task.getId());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setPriority(task.getPriority());
        taskResponse.setStatus(task.getStatus());

        User assignedUser = task.getAssignedTo();

        if(assignedUser != null){

            UserResponse userResponse = new UserResponse();

            userResponse.setId(assignedUser.getId());
            userResponse.setName(assignedUser.getName());
            userResponse.setEmail(assignedUser.getEmail());
            userResponse.setRole(assignedUser.getRole());
            taskResponse.setAssignedTo(userResponse);
        }

        Project project = task.getProject();

        if(project != null){
            taskResponse.setProjectId(project.getId());
            taskResponse.setProjectName(project.getName());
        }
        return taskResponse;
    }

    private ProjectResponse toProjectResponse(Project project) {

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());

        User user = project.getCreatedBy();

        if (user != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setName(user.getName());
            userResponse.setEmail(user.getEmail());
            userResponse.setRole(user.getRole());
            response.setCreatedBy(userResponse);
        }

        return response;
    }

}
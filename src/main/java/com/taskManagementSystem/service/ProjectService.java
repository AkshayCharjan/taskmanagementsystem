package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.ProjectRequest;
import com.taskManagementSystem.dto.ProjectResponse;
import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.entity.Project;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.exception.BadRequestException;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.ProjectRepository;
import com.taskManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectResponse createProject(ProjectRequest request) {

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.error("Project name is required");
            throw new BadRequestException("Project name is required");
        }


        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        if(request.getCreatedByUserId() != null) {
            User user = userRepository.findById(request.getCreatedByUserId())
                    .orElseThrow(() -> {
                        log.error("User not found id={}", request.getCreatedByUserId());
                        return new ResourceNotFoundException("User not found");
                    });
            project.setCreatedBy(user);
        }

        projectRepository.save(project);

        log.info("Project created successfully id={}", project.getId());

        return toProjectResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::toProjectResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project not found id={}", projectId);
                    return new ResourceNotFoundException("Project not found");
                });

        return toProjectResponse(project);
    }

    public void deleteProject(UUID projectId) {

        if (!projectRepository.existsById(projectId)) {
            log.error("Project not found id={}", projectId);
            throw new ResourceNotFoundException("Project not found");
        }

        projectRepository.deleteById(projectId);

        log.info("Project deleted successfully id={}", projectId);
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
            response.setCreatedBy(userResponse);
        }

        return response;
    }

}
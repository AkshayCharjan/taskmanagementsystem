package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.ProjectRequest;
import com.taskManagementSystem.dto.ProjectResponse;
import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.entity.Project;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.ProjectRepository;
import com.taskManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

        User user = userRepository.findById(request.getCreatedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCreatedBy(user);

        projectRepository.save(project);

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
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return toProjectResponse(project);
    }

    public void deleteProject(UUID projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }

        projectRepository.deleteById(projectId);
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
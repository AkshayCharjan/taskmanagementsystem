package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.TaskRequest;
import com.taskManagementSystem.dto.TaskResponse;
import com.taskManagementSystem.entity.Project;
import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.enums.Priority;
import com.taskManagementSystem.enums.Role;
import com.taskManagementSystem.enums.Status;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.ProjectRepository;
import com.taskManagementSystem.repository.TaskRepository;
import com.taskManagementSystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Project testProject;
    private Task testTask;
    private TaskRequest taskRequest;
    private UUID userId;
    private UUID projectId;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);

        testProject = new Project();
        testProject.setId(projectId);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");

        testTask = new Task();
        testTask.setId(taskId);
        testTask.setTitle("Test Task");
        testTask.setStatus(Status.OPEN);
        testTask.setPriority(Priority.HIGH);
        testTask.setAssignedTo(testUser);
        testTask.setProject(testProject);

        taskRequest = new TaskRequest();
        taskRequest.setTitle("New Task");
        taskRequest.setStatus(Status.OPEN);
        taskRequest.setPriority(Priority.HIGH);
        taskRequest.setAssignedTo(userId);
        taskRequest.setProjectId(projectId);
    }

    @Test
    void createTask_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals(Status.OPEN, response.getStatus());
        assertEquals(Priority.HIGH, response.getPriority());

        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_withoutAssignedUser() {
        taskRequest.setAssignedTo(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        verify(userRepository, never()).findById(any());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskRequest));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_projectNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskRequest));
        verify(taskRepository, never()).save(any());
    }


    @Test
    void updateTask_success() {
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setStatus(Status.IN_PROGRESS);
        updateRequest.setPriority(Priority.MODERATE);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.updateTask(updateRequest, taskId);

        assertNotNull(response);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_taskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskRequest, taskId));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_assignNewUser() {
        UUID newUserId = UUID.randomUUID();
        User newUser = new User();
        newUser.setId(newUserId);
        newUser.setName("New User");

        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setAssignedTo(newUserId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.updateTask(updateRequest, taskId);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(newUserId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_invalidStatusTransition() {
        testTask.setStatus(Status.CLOSED);
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setStatus(Status.OPEN);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        assertThrows(IllegalStateException.class, () -> taskService.updateTask(updateRequest, taskId));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_validStatusTransition_OPEN_to_IN_PROGRESS() {
        testTask.setStatus(Status.OPEN);
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setStatus(Status.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.updateTask(updateRequest, taskId);

        assertNotNull(response);
        verify(taskRepository, times(1)).save(any(Task.class));
    }


    @Test
    void deleteTask_success() {
        taskService.deleteTask(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }


    @Test
    void getFilteredTasks_noFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getFilteredTasks(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void getFilteredTasks_filterByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));

        when(taskRepository.findByStatus(Status.OPEN, pageable)).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getFilteredTasks(Status.OPEN, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findByStatus(Status.OPEN, pageable);
    }

    @Test
    void getFilteredTasks_filterByPriority() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));

        when(taskRepository.findByPriority(Priority.HIGH, pageable)).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getFilteredTasks(null, Priority.HIGH, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findByPriority(Priority.HIGH, pageable);
    }

    @Test
    void getFilteredTasks_filterByStatusAndPriority() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask));

        when(taskRepository.findByStatusAndPriority(Status.OPEN, Priority.HIGH, pageable))
                .thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getFilteredTasks(Status.OPEN, Priority.HIGH, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1))
                .findByStatusAndPriority(Status.OPEN, Priority.HIGH, pageable);
    }


    @Test
    void assignTask_success() {
        UUID newUserId = UUID.randomUUID();
        User newUser = new User();
        newUser.setId(newUserId);
        newUser.setName("Assigned User");
        newUser.setEmail("assigned@example.com");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.assignTask(taskId, newUserId);

        assertNotNull(response);
        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(newUserId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void assignTask_taskNotFound() {
        UUID userId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.assignTask(taskId, userId));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void assignTask_userNotFound() {
        UUID newUserId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(newUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.assignTask(taskId, newUserId));
        verify(taskRepository, never()).save(any());
    }


    @Test
    void validateStatusTransition_OPEN_to_CLOSED() {
        testTask.setStatus(Status.OPEN);
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setStatus(Status.CLOSED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.updateTask(updateRequest, taskId);

        assertNotNull(response);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void validateStatusTransition_IN_PROGRESS_to_CLOSED() {
        testTask.setStatus(Status.IN_PROGRESS);
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setStatus(Status.CLOSED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse response = taskService.updateTask(updateRequest, taskId);

        assertNotNull(response);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void validateStatusTransition_OPEN_to_CLOSED_invalid() {
        testTask.setStatus(Status.OPEN);
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setStatus(Status.CLOSED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        assertDoesNotThrow(() -> taskService.updateTask(updateRequest, taskId));
    }

    @Test
    void validateStatusTransition_OPEN_to_INVALID() {
        testTask.setStatus(Status.OPEN);
        testTask.setStatus(Status.CLOSED);
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setStatus(Status.OPEN);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        assertThrows(IllegalStateException.class, () -> taskService.updateTask(updateRequest, taskId));
    }
}
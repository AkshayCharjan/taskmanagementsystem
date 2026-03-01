package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.CommentRequest;
import com.taskManagementSystem.dto.CommentResponse;
import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.entity.Comment;
import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.exception.BadRequestException;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.CommentRepository;
import com.taskManagementSystem.repository.TaskRepository;
import com.taskManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse createComment(UUID taskId,
                                         UUID userId,
                                         CommentRequest request) {

        log.info("Creating comment for taskId={} by userId={}", taskId, userId);

        if (taskId == null) {
            log.error("TaskId is required");
            throw new BadRequestException("TaskId is required");
        }

        if (userId == null) {
            log.error("UserId is required");
            throw new BadRequestException("UserId is required");
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            log.error("Comment content is required");
            throw new BadRequestException("Comment content is required");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found id={}", taskId);
                    return new ResourceNotFoundException("Task not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found id={}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setCreatedBy(user);

        commentRepository.save(comment);

        log.info("Comment created successfully id={}", comment.getId());
        return toCommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTask(UUID taskId) {

        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    public void deleteComment(UUID commentId) {

        if (!commentRepository.existsById(commentId)) {
            log.error("Comment not found id={}", commentId);
            throw new ResourceNotFoundException("Comment not found");
        }

        commentRepository.deleteById(commentId);

        log.info("Comment deleted successfully id={}", commentId);
    }

    private CommentResponse toCommentResponse(Comment comment) {

        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setContent(comment.getContent());

        User user = comment.getCreatedBy();

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
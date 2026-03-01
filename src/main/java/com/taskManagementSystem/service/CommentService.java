package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.CommentRequest;
import com.taskManagementSystem.dto.CommentResponse;
import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.entity.Comment;
import com.taskManagementSystem.entity.Task;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.CommentRepository;
import com.taskManagementSystem.repository.TaskRepository;
import com.taskManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setCreatedBy(user);

        commentRepository.save(comment);

        return toCommentResponse(comment);
    }

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
            throw new ResourceNotFoundException("Comment not found");
        }

        commentRepository.deleteById(commentId);
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
            response.setCreatedBy(userResponse);
        }

        return response;
    }

}
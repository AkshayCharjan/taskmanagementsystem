package com.taskManagementSystem.controller;

import com.taskManagementSystem.dto.CommentRequest;
import com.taskManagementSystem.dto.CommentResponse;
import com.taskManagementSystem.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/tasks/{taskId}/comments")
    public CommentResponse createComment(
            @PathVariable UUID taskId,
            @RequestParam UUID userId,
            @RequestBody CommentRequest request) {

        return commentService.createComment(taskId, userId, request);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<CommentResponse> getComments(@PathVariable UUID taskId) {

        return commentService.getCommentsByTask(taskId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable UUID commentId) {

        commentService.deleteComment(commentId);
    }

}
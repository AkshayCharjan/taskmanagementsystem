package com.taskManagementSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommentResponse {

    private UUID id;

    private String content;

    private UserResponse createdBy;

}
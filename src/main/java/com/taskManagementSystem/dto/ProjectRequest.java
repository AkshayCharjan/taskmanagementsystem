package com.taskManagementSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProjectRequest {

    private String name;

    private String description;
}
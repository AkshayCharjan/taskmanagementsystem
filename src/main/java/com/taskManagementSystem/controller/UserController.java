package com.taskManagementSystem.controller;

import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Admin only and should not be exposed: Created for testing purposes
//    @PostMapping
//    public User createUser(@RequestBody User user) {
//        return userService.createUser(user);
//    }

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }
//Admin only
//    @DeleteMapping("/{userId}")
//    public void deleteUser(@PathVariable UUID userId) {
//        userService.deleteUser(userId);
//    }
}
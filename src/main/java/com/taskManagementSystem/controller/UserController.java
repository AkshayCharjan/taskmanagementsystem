package com.taskManagementSystem.controller;

import com.taskManagementSystem.entity.User;
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

    //should not be exposed
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }
}
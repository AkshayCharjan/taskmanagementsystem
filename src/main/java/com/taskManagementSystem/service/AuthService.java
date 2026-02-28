package com.taskManagementSystem.service;

import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(User user){

    }
}

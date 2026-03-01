package com.taskManagementSystem.service;

import com.taskManagementSystem.dto.UserResponse;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        log.info("User created successfully id={}", savedUser.getId());
        return savedUser;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            responses.add(toUserResponse(user));
        }

        return responses;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found id={}", userId);
                    return new RuntimeException("User not found");
                });
        return toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
        log.info("User deleted successfully id={}", userId);
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }
}
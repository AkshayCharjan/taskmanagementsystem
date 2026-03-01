package com.taskManagementSystem.service;

import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found id={}", userId);
                    return new RuntimeException("User not found");
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
        log.info("User deleted successfully id={}", userId);
    }
}
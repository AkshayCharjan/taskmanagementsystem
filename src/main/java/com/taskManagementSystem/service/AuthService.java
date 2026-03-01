package com.taskManagementSystem.service;

import com.taskManagementSystem.security.JwtUtil;
import com.taskManagementSystem.dto.AuthResponse;
import com.taskManagementSystem.dto.LoginRequest;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.exception.BadRequestException;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(User user){
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            log.error("Email is required");
            throw new BadRequestException("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            log.error("Password is required");
            throw new BadRequestException("Password is required");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            log.error("Name is required");
            throw new BadRequestException("Name is required");
        }

        if(userRepository.existsByEmail(user.getEmail())){
            log.error("User already exists with email={}", user.getEmail());
            throw new BadRequestException("User already exists with this email");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        log.info("User registered successfully with email={}", user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest loginRequest){
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            log.error("Email is required");
            throw new BadRequestException("Email is required");
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            log.error("Password is required");
            throw new BadRequestException("Password is required");
        }

        String userEmail = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if(!userRepository.existsByEmail(userEmail)){
            log.error("User not found with email={}", userEmail);
            throw new ResourceNotFoundException("User does not exist");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(password, user.getPassword())){
            log.error("Invalid password for user email={}", userEmail);
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(userEmail);
        log.info("User logged in successfully with email={}", userEmail);
        return new AuthResponse(token);
    }
}

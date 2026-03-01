package com.taskManagementSystem.service;

import com.taskManagementSystem.security.JwtUtil;
import com.taskManagementSystem.dto.AuthResponse;
import com.taskManagementSystem.dto.LoginRequest;
import com.taskManagementSystem.dto.RegisterRequest;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.enums.Role;
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

    public AuthResponse register(RegisterRequest request){
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("Email is required");
            throw new BadRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("Password is required");
            throw new BadRequestException("Password is required");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.error("Name is required");
            throw new BadRequestException("Name is required");
        }

        if(userRepository.existsByEmail(request.getEmail())){
            log.error("User already exists with email={}", request.getEmail());
            throw new BadRequestException("User already exists with this email");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        user.setRole(Role.USER);

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

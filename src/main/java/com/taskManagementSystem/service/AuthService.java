package com.taskManagementSystem.service;

import com.taskManagementSystem.security.JwtUtil;
import com.taskManagementSystem.dto.AuthResponse;
import com.taskManagementSystem.dto.LoginRequest;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.exception.BadRequestException;
import com.taskManagementSystem.exception.ResourceNotFoundException;
import com.taskManagementSystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest loginRequest){
        String userEmail = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if(!userRepository.existsByEmail(userEmail)){
            throw new ResourceNotFoundException("User does not exists");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadRequestException("Invalid password");
        }
        String token = jwtUtil.generateToken(userEmail);
        return new AuthResponse(token);
    }
}

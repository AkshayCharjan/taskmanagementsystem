package com.taskManagementSystem.service;

import com.taskManagementSystem.auth.JwtUtil;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return jwtUtil.generateToken(user.getEmail());
    }

    public String login(String userEmail, String password){
        if(!userRepository.existsByEmail(userEmail)){
            throw new RuntimeException("User does not exists");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        return jwtUtil.generateToken(userEmail);
    }
}

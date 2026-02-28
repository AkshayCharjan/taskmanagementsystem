package com.taskManagementSystem.controller;


import com.taskManagementSystem.dto.AuthResponse;
import com.taskManagementSystem.dto.LoginRequest;
import com.taskManagementSystem.entity.User;
import com.taskManagementSystem.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody User user){
        return authService.register(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
}

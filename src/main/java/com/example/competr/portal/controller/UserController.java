package com.example.competr.portal.controller;

import com.example.competr.portal.entity.User;
import com.example.competr.portal.request.LoginRequest;
import com.example.competr.portal.response.LoginResponse;
import com.example.competr.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        log.info("Received registration request: phoneNumber={}, userName={}", user.getPhoneNumber(), user.getUserName());
        try {
            User savedUser = userService.registerUser(user);
            log.info("Registration successful: userId={}, phoneNumber={}", savedUser.getId(), savedUser.getPhoneNumber());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            log.error("Registration failed for phoneNumber={}: {}", user.getPhoneNumber(), e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        if (loginResponse.isStatus()) {
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(401).body(loginResponse);
        }
    }


}

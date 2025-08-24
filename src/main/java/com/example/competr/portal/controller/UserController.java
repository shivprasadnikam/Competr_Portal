package com.example.competr.portal.controller;

import com.example.competr.portal.dto.PlayerDto;
import com.example.competr.portal.request.LoginRequest;
import com.example.competr.portal.response.LoginResponse;
import com.example.competr.portal.response.RegisterUserResponse;
import com.example.competr.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/players")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public RegisterUserResponse register(@RequestBody PlayerDto playerDto) {
        log.info("Received registration request: phoneNumber={}, userName={}", playerDto.getPhoneNumber(), playerDto.getUserName());
        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        try {
             registerUserResponse = userService.registerUser(playerDto);
            log.info("Registration successful: userName={}, phoneNumber={}",playerDto.getUserName(), playerDto.getPhoneNumber());
            return registerUserResponse;
        } catch (Exception e) {
            log.error("Registration failed for phoneNumber={}: {}", playerDto.getPhoneNumber(), e.getMessage());
            return registerUserResponse;
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

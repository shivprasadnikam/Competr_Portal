package com.example.competr.portal.service;

import com.example.competr.portal.dto.PlayerDto;
import com.example.competr.portal.request.LoginRequest;
import com.example.competr.portal.response.LoginResponse;
import com.example.competr.portal.response.RegisterUserResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public RegisterUserResponse registerUser(PlayerDto playerDto);
    public LoginResponse loginUser(LoginRequest loginRequest);
}


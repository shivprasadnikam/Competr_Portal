package com.example.competr.portal.service;

import com.example.competr.portal.entity.User;
import com.example.competr.portal.request.LoginRequest;
import com.example.competr.portal.response.LoginResponse;
import com.example.competr.portal.response.RegisterUserResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    public RegisterUserResponse registerUser(User user);
    public LoginResponse loginUser(LoginRequest loginRequest);
}


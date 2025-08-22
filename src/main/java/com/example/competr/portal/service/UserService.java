package com.example.competr.portal.service;

import com.example.competr.portal.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    public User registerUser(User user);
    public Optional<User> loginUser(String phoneNumber, String password);
}


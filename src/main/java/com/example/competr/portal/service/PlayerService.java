package com.example.competr.portal.service;

import com.example.competr.portal.dto.PlayerDto;
import com.example.competr.portal.request.LoginRequest;
import com.example.competr.portal.response.LoginResponse;
import com.example.competr.portal.response.RegisterUserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlayerService {
     RegisterUserResponse registerUser(PlayerDto playerDto);
     LoginResponse loginUser(LoginRequest loginRequest);
     List<String> searchPlayersByName(String name);

}


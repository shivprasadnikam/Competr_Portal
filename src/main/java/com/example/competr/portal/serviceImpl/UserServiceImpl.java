package com.example.competr.portal.serviceImpl;

import com.example.competr.portal.dto.PlayerDto;
import com.example.competr.portal.entity.PlayerEntity;
import com.example.competr.portal.repository.PlayerRepository;
import com.example.competr.portal.request.LoginRequest;
import com.example.competr.portal.response.LoginResponse;
import com.example.competr.portal.response.RegisterUserResponse;
import com.example.competr.portal.service.UserService;
import com.example.competr.portal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final PlayerRepository playerRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public RegisterUserResponse registerUser(PlayerDto playerDto) {
        log.info("Registering user with phoneNumber: {}", playerDto.getPhoneNumber());
        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        try {
            Optional<PlayerEntity> existingUser = playerRepository.findByPhoneNumber(playerDto.getPhoneNumber());
            if (existingUser.isPresent()) {
                registerUserResponse.setStatus(false);
                registerUserResponse.setErrorMessage("User with this phone number already exists");
                return registerUserResponse;
            }

            // Encode password and map DTO → Entity
            playerDto.setPassword(passwordEncoder.encode(playerDto.getPassword()));
            PlayerEntity playerEntity = convertDtoToEntity(playerDto);

            PlayerEntity savedEntity = playerRepository.save(playerEntity);

            registerUserResponse.setStatus(true);
            log.info("User registered successfully with id: {}", savedEntity.getId());
            return registerUserResponse;
        } catch (Exception e) {
            log.error("User registration failed for phoneNumber: {}, error: {}", playerDto.getPhoneNumber(), e.getMessage(), e);
            registerUserResponse.setStatus(false);
            registerUserResponse.setErrorMessage("Registration failed due to server error");
            return registerUserResponse;
        }
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        String phoneNumber = loginRequest.getMobile();
        log.info("Login attempt for phoneNumber: {}", phoneNumber);

        Optional<PlayerEntity> userOpt = playerRepository.findByPhoneNumber(phoneNumber);
        LoginResponse loginResponse = new LoginResponse();

        if (userOpt.isPresent()) {
            PlayerEntity playerEntity = userOpt.get();
            boolean matches = passwordEncoder.matches(loginRequest.getPassword(), playerEntity.getPassword());
            log.info("Password match for phoneNumber {}: {}", phoneNumber, matches);

            if (matches) {
                String token = jwtUtil.generateToken(playerEntity.getUserName());
                log.info("Login successful for userId: {}, phoneNumber: {}", playerEntity.getId(), phoneNumber);

                loginResponse.setUserId(String.valueOf(playerEntity.getId()));
                loginResponse.setStatus(true);
                loginResponse.setToken(token);
                loginResponse.setUserName(playerEntity.getUserName());
            } else {
                log.warn("Invalid password for phoneNumber: {}", phoneNumber);
                loginResponse.setStatus(false);
                loginResponse.setErrorMessage("Invalid phone number or password");
            }
        } else {
            log.warn("User not found with phoneNumber: {}", phoneNumber);
            loginResponse.setStatus(false);
            loginResponse.setErrorMessage("Invalid phone number or password");
        }
        return loginResponse;
    }

    private PlayerEntity convertDtoToEntity(PlayerDto dto) {
        PlayerEntity entity = new PlayerEntity();
        entity.setUserName(dto.getUserName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setPassword(dto.getPassword());
        return entity;
    }
}

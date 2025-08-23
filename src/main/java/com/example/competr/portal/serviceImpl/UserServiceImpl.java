package com.example.competr.portal.serviceImpl;

import com.example.competr.portal.entity.User;
import com.example.competr.portal.repository.UserRepository;
import com.example.competr.portal.request.LoginRequest;
import com.example.competr.portal.response.LoginResponse;
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

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User registerUser(User user) {
        log.info("Registering user with phoneNumber: {}", user.getPhoneNumber());
        try {
            // Check if phone number already exists
            Optional<User> existingUser = userRepository.findByPhoneNumber(user.getPhoneNumber());
            if (existingUser.isPresent()) {
                throw new IllegalArgumentException("User with this phone number already exists");
            }

            // Encode password and save new user
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            log.info("User registered successfully with id: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            log.error("User registration failed for phoneNumber: {}, error: {}", user.getPhoneNumber(), e.getMessage());
            throw e;
        }
    }


    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        String phoneNumber = loginRequest.getMobile();
        log.info("Login attempt for phoneNumber: {}", phoneNumber);

        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        LoginResponse loginResponse = new LoginResponse();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            log.info("Password match for phoneNumber {}: {}", phoneNumber, matches);

            if (matches) {
                String token = jwtUtil.generateToken(user.getUserName());
                log.info("Login successful for userId: {}, phoneNumber: {}", user.getId(), phoneNumber);

                loginResponse.setUserId(user.getId());
                loginResponse.setStatus(true);
                loginResponse.setToken(token);
                return loginResponse;
            } else {
                log.warn("Invalid password for phoneNumber: {}", phoneNumber);
                loginResponse.setUserId(user.getId());
                loginResponse.setStatus(false);
                loginResponse.setErrorMessage("Invalid phone number or password");
                return loginResponse;
            }
        } else {
            log.warn("User not found with phoneNumber: {}", phoneNumber);
            loginResponse.setStatus(false);
            loginResponse.setErrorMessage("Invalid phone number or password");
            return loginResponse;
        }
    }
}

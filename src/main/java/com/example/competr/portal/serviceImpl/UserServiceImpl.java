package com.example.competr.portal.serviceImpl;

import com.example.competr.portal.entity.User;
import com.example.competr.portal.repository.UserRepository;
import com.example.competr.portal.service.UserService;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User registerUser(User user) {
        log.info("Registering user with phoneNumber: {}", user.getPhoneNumber());
        try {
            // Hash user password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            log.info("User registered successfully with id: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            log.error("Error during user registration for phoneNumber: {}, error: {}", user.getPhoneNumber(), e.getMessage());
            throw e;  // Optionally re-throw to be handled upstream
        }
    }

    @Override
    public Optional<User> loginUser(String phoneNumber, String password) {
        log.info("Login request for phoneNumber: {}", phoneNumber);
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            log.info("Password match for user {}: {}", phoneNumber, passwordMatches);

            if (passwordMatches) {
                log.info("Login successful for user with phoneNumber: {}", phoneNumber);
                return Optional.of(user);
            } else {
                log.warn("Invalid password for user with phoneNumber: {}", phoneNumber);
            }
        } else {
            log.warn("User not found with phoneNumber: {}", phoneNumber);
        }

        return Optional.empty();
    }
}

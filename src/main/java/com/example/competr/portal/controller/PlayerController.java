package com.example.competr.portal.controller;

import com.example.competr.portal.dto.*;
import com.example.competr.portal.entity.PlayerEntity;
import com.example.competr.portal.repository.PlayerRepository;
import com.example.competr.portal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            Optional<PlayerEntity> playerOpt = playerRepository.findByPhoneNumber(request.getMobile());
            
            if (playerOpt.isEmpty()) {
                return ResponseEntity.ok(new AuthResponse(null, null, "error", "Player not found", null));
            }
            
            PlayerEntity player = playerOpt.get();
            
            if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
                return ResponseEntity.ok(new AuthResponse(null, null, "error", "Invalid credentials", null));
            }
            
            String token = jwtUtil.generateToken(player.getUserName());
            
            return ResponseEntity.ok(new AuthResponse(
                token, 
                player.getUserName(), 
                "success", 
                null, 
                player.getUserName()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.ok(new AuthResponse(null, null, "error", "Login failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            // Check if user already exists
            if (playerRepository.findByUserName(request.getUserName()).isPresent()) {
                return ResponseEntity.ok(new AuthResponse(null, null, "error", "Username already exists", null));
            }
            
            if (playerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
                return ResponseEntity.ok(new AuthResponse(null, null, "error", "Phone number already exists", null));
            }
            
            // Create new player
            PlayerEntity player = new PlayerEntity();
            player.setUserName(request.getUserName());
            player.setPhoneNumber(request.getPhoneNumber());
            player.setPassword(passwordEncoder.encode(request.getPassword()));
            
            playerRepository.save(player);
            
            String token = jwtUtil.generateToken(player.getUserName());
            
            return ResponseEntity.ok(new AuthResponse(
                token, 
                player.getUserName(), 
                "success", 
                null, 
                player.getUserName()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.ok(new AuthResponse(null, null, "error", "Registration failed: " + e.getMessage(), null));
        }
    }
}

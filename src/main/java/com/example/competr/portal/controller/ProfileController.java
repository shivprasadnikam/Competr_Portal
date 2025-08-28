package com.example.competr.portal.controller;

import com.example.competr.portal.entity.PlayerEntity;
import com.example.competr.portal.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/{userName}")
    public ResponseEntity<?> getProfile(@PathVariable String userName) {
        try {
            // JWT Authentication check
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("errorMessage", "Authentication required");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            Optional<PlayerEntity> playerOpt = playerRepository.findByUserName(userName);
            
            if (playerOpt.isPresent()) {
                PlayerEntity player = playerOpt.get();
                // Remove password from response for security
                player.setPassword(null);
                return ResponseEntity.ok(player);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("errorMessage", "Player not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to fetch player profile: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

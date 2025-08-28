package com.example.competr.portal.controller;

import com.example.competr.portal.dto.LeaderboardEntry;
import com.example.competr.portal.dto.PlayerRankingResponse;
import com.example.competr.portal.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<?> getLeaderboard(
            @RequestParam(defaultValue = "50") Integer limit) {
        try {
            // JWT Authentication check
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("errorMessage", "Authentication required");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard(limit);
            
            // Create response in expected format
            Map<String, Object> response = new HashMap<>();
            response.put("leaderboard", leaderboard);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to fetch leaderboard: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/player/{userName}")
    public ResponseEntity<?> getPlayerRanking(@PathVariable String userName) {
        try {
            // JWT Authentication check
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("errorMessage", "Authentication required");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            PlayerRankingResponse ranking = leaderboardService.getPlayerRanking(userName);
            return ResponseEntity.ok(ranking);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Player not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("errorMessage", "Player not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to fetch player ranking: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to fetch player ranking: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopPlayers(
            @RequestParam(defaultValue = "ALL") String matchType,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            // JWT Authentication check
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("errorMessage", "Authentication required");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            List<LeaderboardEntry> topPlayers = leaderboardService.getTopPlayers(matchType, limit);
            
            // Create response in expected format
            Map<String, Object> response = new HashMap<>();
            response.put("leaderboard", topPlayers);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to fetch top players: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

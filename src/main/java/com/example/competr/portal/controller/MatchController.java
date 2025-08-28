package com.example.competr.portal.controller;

import com.example.competr.portal.dto.*;
import com.example.competr.portal.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping("/findPlayer")
    public ResponseEntity<?> findPlayer(@RequestBody FindPlayerRequest request) {
        try {
            if (!isAuthenticated()) {
                return createErrorResponse("Authentication required", 401);
            }
            
            List<String> players = matchService.findPlayersByUserName(request.getUserName());
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            return createErrorResponse("Failed to find players: " + e.getMessage(), 500);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createMatch(@RequestBody CreateMatchRequest request) {
        try {
            if (!isAuthenticated()) {
                return createErrorResponse("Authentication required", 401);
            }
            
            Map<String, Object> response = matchService.createMatch(request);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), 400);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return createErrorResponse(e.getMessage(), 404);
            }
            return createErrorResponse("Failed to create match: " + e.getMessage(), 500);
        } catch (Exception e) {
            return createErrorResponse("Failed to create match: " + e.getMessage(), 500);
        }
    }

    @PostMapping("/result")
    public ResponseEntity<Map<String, Object>> saveMatchResult(@RequestBody MatchResultRequest request) {
        try {
            Map<String, Object> response = matchService.saveMatchResult(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return createErrorResponse("Failed to save match result: " + e.getMessage(), 500);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getMatchHistory(
            @RequestParam(required = false) String playerId,
            @RequestParam(required = false, defaultValue = "50") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page) {
        
        try {
            if (!isAuthenticated()) {
                return createErrorResponse("Authentication required", 401);
            }
            
            String currentUser = getCurrentUser();
            Map<String, Object> response = matchService.getMatchHistory(playerId, limit, page, currentUser);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return createErrorResponse("Failed to fetch match history: " + e.getMessage(), 500);
        }
    }

    @PutMapping("/{matchId}/score")
    public ResponseEntity<Map<String, Object>> updateMatchScore(
            @PathVariable String matchId,
            @RequestBody UpdateScoreRequest request) {
        
        try {
            if (!isAuthenticated()) {
                return createErrorResponse("Authentication required", 401);
            }
            
            Map<String, Object> response = matchService.updateMatchScore(matchId, request);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return createErrorResponse(e.getMessage(), 404);
            }
            return createErrorResponse("Failed to update score: " + e.getMessage(), 500);
        } catch (Exception e) {
            return createErrorResponse("Failed to update score: " + e.getMessage(), 500);
        }
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
    
    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, int status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("errorMessage", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}

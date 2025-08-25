package com.example.competr.portal.controller;

import com.example.competr.portal.dto.*;
import com.example.competr.portal.entity.MatchEntity;
import com.example.competr.portal.repository.MatchRepository;
import com.example.competr.portal.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @PostMapping("/findPlayer")
    public ResponseEntity<List<String>> findPlayer(@RequestBody FindPlayerRequest request) {
        try {
            List<String> players = playerRepository.findUserNamesByUserNameContainingIgnoreCase(request.getUserName());
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createMatch(@RequestBody CreateMatchRequest request) {
        try {
            String matchId = "match_" + System.currentTimeMillis();
            
            MatchEntity match = new MatchEntity();
            match.setMatchId(matchId);
            match.setPlayers(request.getPlayerIds());
            match.setMatchType(MatchEntity.MatchType.valueOf(request.getMatchType()));
            match.setStatus(MatchEntity.MatchStatus.CREATED);
            match.setScores(new ArrayList<>(Collections.nCopies(request.getPlayerIds().size(), 0)));
            
            matchRepository.save(match);
            
            Map<String, Object> response = new HashMap<>();
            response.put("matchId", matchId);
            response.put("status", "created");
            response.put("players", request.getPlayerIds());
            response.put("matchType", request.getMatchType());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to create match: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PostMapping("/result")
    public ResponseEntity<Map<String, Object>> saveMatchResult(@RequestBody MatchResultRequest request) {
        try {
            MatchEntity match = matchRepository.findById(request.getMatchId())
                .orElse(new MatchEntity());
            
            match.setMatchId(request.getMatchId());
            match.setPlayers(request.getPlayers());
            match.setScores(request.getScores());
            match.setWinner(request.getWinner());
            match.setMatchType(MatchEntity.MatchType.valueOf(request.getMatchType()));
            match.setScoreTarget(request.getScoreTarget());
            match.setDuration(request.getDuration());
            match.setStartTime(request.getStartTime());
            match.setEndTime(request.getEndTime());
            match.setStatus(MatchEntity.MatchStatus.COMPLETED);
            
            matchRepository.save(match);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("matchId", request.getMatchId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to save match result: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<MatchHistoryResponse>> getMatchHistory(
            @RequestParam(required = false) String playerId,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        
        try {
            List<MatchEntity> matches;
            
            if (playerId != null && !playerId.isEmpty()) {
                matches = matchRepository.findByPlayersContaining(playerId);
            } else {
                matches = matchRepository.findAllOrderByCreatedAtDesc();
            }
            
            List<MatchHistoryResponse> response = matches.stream()
                .limit(limit)
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PutMapping("/{matchId}/score")
    public ResponseEntity<Map<String, Object>> updateMatchScore(
            @PathVariable String matchId,
            @RequestBody UpdateScoreRequest request) {
        
        try {
            Optional<MatchEntity> matchOpt = matchRepository.findById(matchId);
            
            if (matchOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("errorMessage", "Match not found");
                return ResponseEntity.ok(errorResponse);
            }
            
            MatchEntity match = matchOpt.get();
            match.setScores(request.getScores());
            match.setServing(request.getServing());
            match.setServesLeft(request.getServesLeft());
            match.setStatus(MatchEntity.MatchStatus.IN_PROGRESS);
            
            matchRepository.save(match);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("matchId", matchId);
            response.put("scores", request.getScores());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("errorMessage", "Failed to update score: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    private MatchHistoryResponse convertToHistoryResponse(MatchEntity match) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        String date = match.getCreatedAt() != null ? match.getCreatedAt().format(dateFormatter) : "";
        String time = match.getCreatedAt() != null ? match.getCreatedAt().format(timeFormatter) : "";
        String duration = match.getDuration() != null ? match.getDuration() + " min" : "";
        
        return new MatchHistoryResponse(
            match.getMatchId(),
            date,
            time,
            match.getPlayers(),
            match.getScores(),
            match.getMatchType().toString(),
            duration,
            match.getWinner()
        );
    }
}

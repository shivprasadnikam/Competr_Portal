package com.example.competr.portal.serviceImpl;

import com.example.competr.portal.dto.*;
import com.example.competr.portal.entity.MatchEntity;
import com.example.competr.portal.repository.MatchRepository;
import com.example.competr.portal.repository.PlayerRepository;
import com.example.competr.portal.service.LeaderboardService;
import com.example.competr.portal.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    @Override
    public List<String> findPlayersByUserName(String userName) {
        return playerRepository.findUserNamesByUserNameContainingIgnoreCase(userName);
    }

    @Override
    public Map<String, Object> createMatch(CreateMatchRequest request) {
        // Validate request data
        validateCreateMatchRequest(request);
        
        // Validate that all players exist in the system
        validatePlayersExist(request.getPlayerIds());
        
        // Check for duplicate players
        validateNoDuplicatePlayers(request.getPlayerIds());
        
        String matchId = "match_" + System.currentTimeMillis();
        String matchType = request.getMatchType().toUpperCase().trim();
        
        MatchEntity match = new MatchEntity();
        match.setMatchId(matchId);
        match.setPlayers(request.getPlayerIds());
        match.setMatchType(MatchEntity.MatchType.valueOf(matchType));
        match.setStatus(MatchEntity.MatchStatus.CREATED);
        match.setScores(new ArrayList<>(Collections.nCopies(request.getPlayerIds().size(), 0)));
        
        // Set individual player fields to satisfy database constraints
        List<String> playerIds = request.getPlayerIds();
        if (!playerIds.isEmpty()) {
            match.setPlayer1Id(playerIds.get(0));
            if (playerIds.size() > 1) match.setPlayer2Id(playerIds.get(1));
            if (playerIds.size() > 2) match.setPlayer3Id(playerIds.get(2));
            if (playerIds.size() > 3) match.setPlayer4Id(playerIds.get(3));
        }
        
        // Set match date if provided, otherwise use current time
        if (request.getMatchDate() != null) {
            match.setStartTime(request.getMatchDate());
            match.setMatchDate(request.getMatchDate());
        } else {
            match.setMatchDate(LocalDateTime.now());
        }
        
        matchRepository.save(match);
        
        Map<String, Object> response = new HashMap<>();
        response.put("matchId", matchId);
        response.put("status", "created");
        response.put("players", request.getPlayerIds());
        response.put("matchType", matchType);
        
        return response;
    }

    @Override
    public Map<String, Object> saveMatchResult(MatchResultRequest request) {
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
        
        // Update leaderboard stats with ELO rating system
        updateLeaderboardStatsWithELO(request.getPlayers(), request.getWinner());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("matchId", request.getMatchId());
        
        return response;
    }

    @Override
    public Map<String, Object> getMatchHistory(String playerId, Integer limit, Integer page, String currentUser) {
        // If no specific playerId is provided, use the authenticated user
        String targetPlayerId = (playerId != null && !playerId.isEmpty()) ? playerId : currentUser;
        
        // Validate limit and page parameters
        if (limit <= 0 || limit > 100) {
            limit = 50; // Default limit
        }
        if (page < 0) {
            page = 0; // Default page
        }
        
        List<MatchEntity> matches = matchRepository.findByPlayersContaining(targetPlayerId);
        
        // Apply pagination manually since we're using a custom query
        List<MatchHistoryResponse> response = matches.stream()
            .skip((long) page * limit)
            .limit(limit)
            .map(this::convertToHistoryResponse)
            .collect(Collectors.toList());
        
        // Create response in expected format
        Map<String, Object> matchHistoryResponse = new HashMap<>();
        matchHistoryResponse.put("matches", response);
        
        return matchHistoryResponse;
    }

    @Override
    public Map<String, Object> updateMatchScore(String matchId, UpdateScoreRequest request) {
        Optional<MatchEntity> matchOpt = matchRepository.findById(matchId);
        
        if (matchOpt.isEmpty()) {
            throw new RuntimeException("Match not found");
        }
        
        MatchEntity match = matchOpt.get();
        match.setScores(request.getScores());
        match.setServing(request.getServing());
        match.setServesLeft(request.getServesLeft());
        
        // Set status based on gameStatus field
        if ("IN_PROGRESS".equals(request.getGameStatus())) {
            match.setStatus(MatchEntity.MatchStatus.IN_PROGRESS);
        } else if ("COMPLETED".equals(request.getGameStatus())) {
            match.setStatus(MatchEntity.MatchStatus.COMPLETED);
        }
        
        matchRepository.save(match);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("matchId", matchId);
        response.put("scores", request.getScores());
        response.put("currentSet", request.getCurrentSet());
        response.put("gameStatus", request.getGameStatus());
        
        return response;
    }

    @Override
    public MatchHistoryResponse convertToHistoryResponse(MatchEntity match) {
        DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        String matchDate = match.getCreatedAt() != null ? match.getCreatedAt().format(isoFormatter) : "";
        
        // Ensure scores list is not null for created matches
        List<Integer> scores = match.getScores() != null ? match.getScores() : new ArrayList<>();
        
        return new MatchHistoryResponse(
            match.getMatchId(),
            match.getPlayers() != null ? match.getPlayers() : new ArrayList<>(),
            scores,
            match.getWinner(),
            match.getMatchType() != null ? match.getMatchType().toString() : "SINGLES",
            match.getDuration(),
            matchDate
        );
    }

    private void validateCreateMatchRequest(CreateMatchRequest request) {
        if (request.getPlayerIds() == null || request.getPlayerIds().isEmpty()) {
            throw new IllegalArgumentException("Player IDs are required");
        }
        
        if (request.getMatchType() == null || request.getMatchType().trim().isEmpty()) {
            throw new IllegalArgumentException("Match type is required");
        }
        
        // Validate match type and player count
        String matchType = request.getMatchType().toUpperCase().trim();
        if (!matchType.equals("SINGLES") && !matchType.equals("DOUBLES")) {
            throw new IllegalArgumentException("Invalid match type. Must be SINGLES or DOUBLES");
        }
        
        // Validate player count based on match type
        int playerCount = request.getPlayerIds().size();
        if (matchType.equals("SINGLES") && playerCount != 2) {
            throw new IllegalArgumentException("Singles match requires exactly 2 players");
        }
        
        if (matchType.equals("DOUBLES") && playerCount != 4) {
            throw new IllegalArgumentException("Doubles match requires exactly 4 players");
        }
    }

    private void validatePlayersExist(List<String> playerIds) {
        for (String playerId : playerIds) {
            if (playerId == null || playerId.trim().isEmpty()) {
                throw new IllegalArgumentException("Player ID cannot be empty");
            }
            
            if (!playerRepository.existsById(playerId.trim())) {
                throw new RuntimeException("Player not found: " + playerId);
            }
        }
    }

    private void validateNoDuplicatePlayers(List<String> playerIds) {
        Set<String> uniquePlayers = new HashSet<>(playerIds);
        if (uniquePlayers.size() != playerIds.size()) {
            throw new IllegalArgumentException("Duplicate players are not allowed");
        }
    }

    private void updateLeaderboardStatsWithELO(List<String> players, Integer winnerIndex) {
        try {
            if (players.size() == 2 && winnerIndex != null) {
                String player1 = players.get(0);
                String player2 = players.get(1);
                String winner = players.get(winnerIndex);
                
                // Update both players with ELO calculation
                leaderboardService.updatePlayerStatsWithELO(player1, player2, player1.equals(winner));
                leaderboardService.updatePlayerStatsWithELO(player2, player1, player2.equals(winner));
            }
        } catch (Exception e) {
            // Log error but don't fail the match result
            System.err.println("Failed to update leaderboard stats: " + e.getMessage());
        }
    }
}

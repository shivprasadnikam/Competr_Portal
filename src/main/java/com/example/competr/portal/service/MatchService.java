package com.example.competr.portal.service;

import com.example.competr.portal.dto.*;
import com.example.competr.portal.entity.MatchEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface MatchService {
    
    List<String> findPlayersByUserName(String userName);
    
    Map<String, Object> createMatch(CreateMatchRequest request);
    
    Map<String, Object> saveMatchResult(MatchResultRequest request);
    
    Map<String, Object> getMatchHistory(String playerId, Integer limit, Integer page, String currentUser);
    
    Map<String, Object> updateMatchScore(String matchId, UpdateScoreRequest request);
    
    MatchHistoryResponse convertToHistoryResponse(MatchEntity match);
}

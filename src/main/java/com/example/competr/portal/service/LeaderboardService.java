package com.example.competr.portal.service;

import com.example.competr.portal.dto.LeaderboardEntry;
import com.example.competr.portal.dto.PlayerRankingResponse;

import java.util.List;

public interface LeaderboardService {
    List<LeaderboardEntry> getLeaderboard(Integer limit);
    PlayerRankingResponse getPlayerRanking(String userName);
    List<LeaderboardEntry> getTopPlayers(String matchType, Integer limit);
    void updatePlayerStats(String userName, boolean won);
    void updatePlayerStatsWithELO(String playerName, String opponentName, boolean playerWon);
}

package com.example.competr.portal.serviceImpl;

import com.example.competr.portal.dto.LeaderboardEntry;
import com.example.competr.portal.dto.PlayerRankingResponse;
import com.example.competr.portal.entity.LeaderboardEntity;
import com.example.competr.portal.entity.PlayerEntity;
import com.example.competr.portal.repository.LeaderboardRepository;
import com.example.competr.portal.repository.PlayerRepository;
import com.example.competr.portal.service.LeaderboardService;
import com.example.competr.portal.service.RatingCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RatingCalculationService ratingCalculationService;

    @Override
    public List<LeaderboardEntry> getLeaderboard(Integer limit) {
        // First, ensure all players have leaderboard entries
        ensureAllPlayersHaveLeaderboardEntries();
        
        Pageable pageable = PageRequest.of(0, limit != null ? limit : 50);
        List<LeaderboardEntity> leaderboardEntities = leaderboardRepository.findAllOrderedByPointsDesc(pageable);
        
        return IntStream.range(0, leaderboardEntities.size())
                .mapToObj(i -> {
                    LeaderboardEntity entity = leaderboardEntities.get(i);
                    return new LeaderboardEntry(
                        entity.getPlayer().getUserName(),
                        entity.getPlayer().getUserName(),
                        entity.getPoints(),
                        entity.getMatchesWon(),
                        entity.getMatchesLost(),
                        calculateTier(entity.getPoints()),
                        i + 1,
                        entity.getMatchesPlayed()
                    );
                })
                .toList();
    }

    @Override
    public PlayerRankingResponse getPlayerRanking(String userName) {
        Optional<LeaderboardEntity> leaderboardOpt = leaderboardRepository.findByPlayerId(userName);
        
        if (leaderboardOpt.isEmpty()) {
            throw new RuntimeException("Player not found");
        }
        
        LeaderboardEntity leaderboard = leaderboardOpt.get();
        Long rank = leaderboardRepository.findRankByPointsAndWins(leaderboard.getPoints(), leaderboard.getMatchesWon());
        
        return new PlayerRankingResponse(
            userName,
            rank.intValue(),
            leaderboard.getPoints(),
            leaderboard.getMatchesWon(),
            leaderboard.getMatchesLost(),
            calculateTier(leaderboard.getPoints()),
            leaderboard.getMatchesPlayed()
        );
    }

    @Override
    public List<LeaderboardEntry> getTopPlayers(String matchType, Integer limit) {
        // For now, we'll return top players regardless of match type
        // This can be enhanced later to filter by match type
        return getLeaderboard(limit != null ? limit : 10);
    }

    @Override
    public void updatePlayerStats(String userName, boolean won) {
        // Note: This method is now primarily for updating match counts
        // ELO rating calculations are handled in MatchController with opponent data
        Optional<PlayerEntity> playerOpt = playerRepository.findById(userName);
        if (playerOpt.isEmpty()) {
            return;
        }

        PlayerEntity player = playerOpt.get();
        Optional<LeaderboardEntity> leaderboardOpt = leaderboardRepository.findByPlayer(player);
        
        LeaderboardEntity leaderboard;
        if (leaderboardOpt.isPresent()) {
            leaderboard = leaderboardOpt.get();
        } else {
            // Create new leaderboard entry for new player
            leaderboard = new LeaderboardEntity();
            leaderboard.setPlayerId(player.getUserName());
            leaderboard.setPlayer(player);
            leaderboard.setRanking(0);
            leaderboard.setMatchesPlayed(0);
            leaderboard.setMatchesWon(0);
            leaderboard.setMatchesLost(0);
            leaderboard.setPoints(1200); // Starting ELO rating
        }

        // Update match counts only
        leaderboard.setMatchesPlayed(leaderboard.getMatchesPlayed() + 1);
        if (won) {
            leaderboard.setMatchesWon(leaderboard.getMatchesWon() + 1);
        } else {
            leaderboard.setMatchesLost(leaderboard.getMatchesLost() + 1);
        }

        leaderboardRepository.save(leaderboard);
    }

    /**
     * Update player stats with ELO rating calculation
     */
    @Override
    public void updatePlayerStatsWithELO(String playerName, String opponentName, boolean playerWon) {
        Optional<LeaderboardEntity> playerLbOpt = leaderboardRepository.findByPlayerId(playerName);
        Optional<LeaderboardEntity> opponentLbOpt = leaderboardRepository.findByPlayerId(opponentName);

        if (playerLbOpt.isEmpty() || opponentLbOpt.isEmpty()) {
            return;
        }

        LeaderboardEntity playerLb = playerLbOpt.get();
        LeaderboardEntity opponentLb = opponentLbOpt.get();

        int playerRating = playerLb.getPoints();
        int opponentRating = opponentLb.getPoints();
        int playerGames = playerLb.getMatchesPlayed();

        // Calculate new rating using ELO system
        int newPlayerRating = ratingCalculationService.calculateNewRating(
            playerRating, opponentRating, playerWon, playerGames);

        // Update player stats
        playerLb.setPoints(newPlayerRating);
        playerLb.setMatchesPlayed(playerLb.getMatchesPlayed() + 1);
        if (playerWon) {
            playerLb.setMatchesWon(playerLb.getMatchesWon() + 1);
        } else {
            playerLb.setMatchesLost(playerLb.getMatchesLost() + 1);
        }

        leaderboardRepository.save(playerLb);
    }

    private void ensureAllPlayersHaveLeaderboardEntries() {
        List<PlayerEntity> allPlayers = playerRepository.findAll();
        for (PlayerEntity player : allPlayers) {
            Optional<LeaderboardEntity> existing = leaderboardRepository.findByPlayer(player);
            if (existing.isEmpty()) {
                // Create new leaderboard entry for player
                LeaderboardEntity newEntry = new LeaderboardEntity();
                newEntry.setPlayerId(player.getUserName());
                newEntry.setPlayer(player);
                newEntry.setRanking(0);
                newEntry.setMatchesPlayed(0);
                newEntry.setMatchesWon(0);
                newEntry.setMatchesLost(0);
                newEntry.setPoints(1200); // Starting ELO rating
                leaderboardRepository.save(newEntry);
            }
        }
    }

    private String calculateTier(Integer points) {
        if (points >= 1400) return "Gold";
        if (points >= 1200) return "Silver";
        return "Bronze";
    }
}

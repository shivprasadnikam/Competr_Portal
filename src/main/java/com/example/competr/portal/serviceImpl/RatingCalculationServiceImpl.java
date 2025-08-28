package com.example.competr.portal.serviceImpl;

import com.example.competr.portal.entity.LeaderboardEntity;
import com.example.competr.portal.entity.MatchEntity;
import com.example.competr.portal.repository.LeaderboardRepository;
import com.example.competr.portal.repository.MatchRepository;
import com.example.competr.portal.service.RatingCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingCalculationServiceImpl implements RatingCalculationService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Override
    public int calculateNewRating(int playerRating, int opponentRating, boolean playerWon, int playerGamesPlayed) {
        double expectedScore = calculateExpectedScore(playerRating, opponentRating);
        int kFactor = getKFactor(playerGamesPlayed);
        double actualScore = playerWon ? 1.0 : 0.0;
        
        int ratingChange = (int) Math.round(kFactor * (actualScore - expectedScore));
        int newRating = playerRating + ratingChange;
        
        // Ensure rating doesn't go below 100
        return Math.max(100, newRating);
    }

    @Override
    public double calculateExpectedScore(int playerRating, int opponentRating) {
        double ratingDifference = opponentRating - playerRating;
        return 1.0 / (1.0 + Math.pow(10, ratingDifference / 400.0));
    }

    @Override
    public int getKFactor(int gamesPlayed) {
        if (gamesPlayed < 30) {
            return 32; // New players - higher volatility
        } else if (gamesPlayed < 100) {
            return 24; // Intermediate players
        } else {
            return 16; // Experienced players - more stable
        }
    }

    @Override
    @Transactional
    public void recalculateWeeklyRatings() {
        // Get all matches from the past week that haven't been processed for weekly rating
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<MatchEntity> weeklyMatches = matchRepository.findByCreatedAtAfterAndStatus(
            oneWeekAgo, MatchEntity.MatchStatus.COMPLETED);

        // Process each match for rating recalculation
        for (MatchEntity match : weeklyMatches) {
            processMatchForRating(match);
        }
    }

    private void processMatchForRating(MatchEntity match) {
        List<String> players = match.getPlayers();
        Integer winnerIndex = match.getWinner();

        // For now, handle 2-player matches (can be extended for multiplayer)
        if (players.size() == 2 && winnerIndex != null) {
            String player1 = players.get(0);
            String player2 = players.get(1);
            String winner = players.get(winnerIndex);

            Optional<LeaderboardEntity> lb1 = leaderboardRepository.findByPlayerId(player1);
            Optional<LeaderboardEntity> lb2 = leaderboardRepository.findByPlayerId(player2);

            if (lb1.isPresent() && lb2.isPresent()) {
                LeaderboardEntity leaderboard1 = lb1.get();
                LeaderboardEntity leaderboard2 = lb2.get();

                int rating1 = leaderboard1.getPoints();
                int rating2 = leaderboard2.getPoints();

                boolean player1Won = player1.equals(winner);
                boolean player2Won = player2.equals(winner);

                // Calculate new ratings
                int newRating1 = calculateNewRating(rating1, rating2, player1Won, leaderboard1.getMatchesPlayed());
                int newRating2 = calculateNewRating(rating2, rating1, player2Won, leaderboard2.getMatchesPlayed());

                // Update ratings
                leaderboard1.setPoints(newRating1);
                leaderboard2.setPoints(newRating2);

                leaderboardRepository.save(leaderboard1);
                leaderboardRepository.save(leaderboard2);
            }
        }
    }

    /**
     * Calculate rating change preview without saving
     */
    public int previewRatingChange(int playerRating, int opponentRating, boolean playerWon, int playerGamesPlayed) {
        int newRating = calculateNewRating(playerRating, opponentRating, playerWon, playerGamesPlayed);
        return newRating - playerRating;
    }
}

package com.example.competr.portal.service;

public interface RatingCalculationService {
    
    /**
     * Calculate new rating based on ELO system
     * @param playerRating Current player rating
     * @param opponentRating Opponent's rating
     * @param playerWon Whether the player won
     * @param playerGamesPlayed Total games played by player
     * @return New rating for the player
     */
    int calculateNewRating(int playerRating, int opponentRating, boolean playerWon, int playerGamesPlayed);
    
    /**
     * Calculate expected score (probability of winning)
     * @param playerRating Player's current rating
     * @param opponentRating Opponent's rating
     * @return Expected score between 0 and 1
     */
    double calculateExpectedScore(int playerRating, int opponentRating);
    
    /**
     * Get K-factor based on player experience
     * @param gamesPlayed Total games played
     * @return K-factor for rating calculation
     */
    int getKFactor(int gamesPlayed);
    
    /**
     * Recalculate all ratings for the current week
     */
    void recalculateWeeklyRatings();
}

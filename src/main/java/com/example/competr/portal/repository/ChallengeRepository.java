package com.example.competr.portal.repository;

import com.example.competr.portal.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<ChallengeEntity, String> {
    
    @Query("SELECT c FROM ChallengeEntity c WHERE c.fromPlayer = :playerId OR c.toPlayer = :playerId ORDER BY c.createdAt DESC")
    List<ChallengeEntity> findAllChallengesForPlayer(@Param("playerId") String playerId);
    
    @Query("SELECT c FROM ChallengeEntity c WHERE c.fromPlayer = :playerId ORDER BY c.createdAt DESC")
    List<ChallengeEntity> findSentChallenges(@Param("playerId") String playerId);
    
    @Query("SELECT c FROM ChallengeEntity c WHERE c.toPlayer = :playerId ORDER BY c.createdAt DESC")
    List<ChallengeEntity> findReceivedChallenges(@Param("playerId") String playerId);
    
    @Query("SELECT c FROM ChallengeEntity c WHERE ((c.fromPlayer = :player1 AND c.toPlayer = :player2) OR (c.fromPlayer = :player2 AND c.toPlayer = :player1)) AND c.status = 'PENDING'")
    List<ChallengeEntity> findPendingChallengesBetweenPlayers(@Param("player1") String player1, @Param("player2") String player2);
}

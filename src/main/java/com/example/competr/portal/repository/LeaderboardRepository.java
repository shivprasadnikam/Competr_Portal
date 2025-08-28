package com.example.competr.portal.repository;

import com.example.competr.portal.entity.LeaderboardEntity;
import com.example.competr.portal.entity.PlayerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntity, Long> {
    
    @Query("SELECT l FROM LeaderboardEntity l ORDER BY l.points DESC, l.matchesWon DESC")
    List<LeaderboardEntity> findAllOrderedByPointsDesc(Pageable pageable);
    
    @Query("SELECT l FROM LeaderboardEntity l ORDER BY l.points DESC, l.matchesWon DESC")
    List<LeaderboardEntity> findAllOrderedByPointsDesc();
    
    Optional<LeaderboardEntity> findByPlayer(PlayerEntity player);
    
    Optional<LeaderboardEntity> findByPlayerId(String playerId);
    
    @Query("SELECT COUNT(l) + 1 FROM LeaderboardEntity l WHERE l.points > :points OR (l.points = :points AND l.matchesWon > :wins)")
    Long findRankByPointsAndWins(@Param("points") Integer points, @Param("wins") Integer wins);
}

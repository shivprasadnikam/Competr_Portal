package com.example.competr.portal.repository;

import com.example.competr.portal.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {
    
    @Query("SELECT m FROM MatchEntity m WHERE :playerId MEMBER OF m.players ORDER BY m.createdAt DESC")
    List<MatchEntity> findByPlayersContaining(@Param("playerId") String playerId);
    
    @Query("SELECT m FROM MatchEntity m ORDER BY m.createdAt DESC")
    List<MatchEntity> findAllOrderByCreatedAtDesc();
}
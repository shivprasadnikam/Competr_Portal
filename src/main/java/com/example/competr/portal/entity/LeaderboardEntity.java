package com.example.competr.portal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "leaderboard")
public class LeaderboardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", unique = true, nullable = false)
    private String playerId;
    
    @OneToOne(optional = false)
    @JoinColumn(name = "player_id", referencedColumnName = "user_name", insertable = false, updatable = false)
    private PlayerEntity player;

    @Column(nullable = false)
    private Integer ranking;

    private Integer matchesPlayed = 0;
    private Integer matchesWon = 0;
    private Integer matchesLost = 0;
    private Integer points = 0;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }

}

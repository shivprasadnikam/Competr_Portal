package com.example.competr.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "matches")
public class MatchEntity {
    
    @Id
    @Column(name = "match_id")
    private String matchId;
    
    @ElementCollection
    @CollectionTable(name = "match_players", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "player_id")
    private List<String> players;
    
    @ElementCollection
    @CollectionTable(name = "match_scores", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "score")
    private List<Integer> scores;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 20)
    private MatchType matchType;
    
    @Column(name = "winner_index")
    private Integer winner;
    
    @Column(name = "score_target")
    private Integer scoreTarget;
    
    @Column(name = "duration_minutes")
    private Integer duration;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "serving_player")
    private Integer serving;
    
    @Column(name = "serves_left")
    private Integer servesLeft;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MatchStatus status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum MatchType {
        SINGLES, DOUBLES
    }
    
    public enum MatchStatus {
        CREATED, IN_PROGRESS, COMPLETED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = MatchStatus.CREATED;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

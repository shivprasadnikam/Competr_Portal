package com.example.competr.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "challenges")
public class ChallengeEntity {
    
    @Id
    @Column(name = "challenge_id")
    private String id;
    
    @Column(name = "from_player", nullable = false)
    private String fromPlayer;
    
    @Column(name = "to_player", nullable = false)
    private String toPlayer;
    
    @Column(name = "message")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false)
    private MatchType matchType;
    
    @Column(name = "score_target", nullable = false)
    private Integer scoreTarget;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChallengeStatus status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "match_id")
    private String matchId; // Set when challenge is accepted and match is created
    
    public enum MatchType {
        SINGLES, DOUBLES
    }
    
    public enum ChallengeStatus {
        PENDING, ACCEPTED, DECLINED, CANCELLED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ChallengeStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

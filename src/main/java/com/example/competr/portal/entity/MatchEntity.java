package com.example.competr.portal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "matches")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String matchType;  // e.g., singles, doubles

    @ManyToOne(optional = false)
    @JoinColumn(name = "player1_id")
    private PlayerEntity player1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player2_id")
    private PlayerEntity player2;

    @ManyToOne
    @JoinColumn(name = "player3_id")
    private PlayerEntity player3; // Nullable for singles

    @ManyToOne
    @JoinColumn(name = "player4_id")
    private PlayerEntity player4; // Nullable for singles

    private Integer player1Score = 0;
    private Integer player2Score = 0;
    private Integer player3Score = 0;
    private Integer player4Score = 0;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private PlayerEntity winner;

    @Column(nullable = false)
    private LocalDateTime matchDate = LocalDateTime.now();

    @Column(length = 255)
    private String venue;

}

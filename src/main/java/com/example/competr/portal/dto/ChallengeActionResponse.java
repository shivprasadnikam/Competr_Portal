package com.example.competr.portal.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeActionResponse {
    private String id;
    private String status;
    private String message;
    private String targetPlayer;
    private String matchId; // Only set when challenge is accepted
}

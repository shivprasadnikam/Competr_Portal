package com.example.competr.portal.dto;

import lombok.Data;

@Data
public class CreateChallengeRequest {
    private String targetPlayer;
    private String message;
    private String matchType;
    private Integer scoreTarget;
}

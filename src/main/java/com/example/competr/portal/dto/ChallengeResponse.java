package com.example.competr.portal.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeResponse {
    private String id;
    private String from;
    private String to;
    private Boolean fromCurrentUser;
    private String message;
    private String matchType;
    private Integer scoreTarget;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

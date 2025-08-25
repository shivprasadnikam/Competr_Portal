package com.example.competr.portal.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MatchResultRequest {
    private List<String> players;
    private List<Integer> scores;
    private Integer winner;
    private String matchType;
    private Integer scoreTarget;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String matchId;
}

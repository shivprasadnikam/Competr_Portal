package com.example.competr.portal.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchHistoryResponse {
    private String matchId;
    private List<String> players;
    private List<Integer> scores;
    private Integer winner;
    private String matchType;
    private Integer duration;
    private String matchDate;
}

package com.example.competr.portal.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntry {
    private String id;
    private String userName;
    private Integer rating;
    private Integer wins;
    private Integer losses;
    private String tier;
    private Integer rank;
    private Integer totalMatches;
}

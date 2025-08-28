package com.example.competr.portal.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRankingResponse {
    private String userName;
    private Integer rank;
    private Integer rating;
    private Integer wins;
    private Integer losses;
    private String tier;
    private Integer totalMatches;
}

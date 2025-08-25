package com.example.competr.portal.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchHistoryResponse {
    private String id;
    private String date;
    private String time;
    private List<String> players;
    private List<Integer> scores;
    private String matchType;
    private String duration;
    private Integer winner;
}

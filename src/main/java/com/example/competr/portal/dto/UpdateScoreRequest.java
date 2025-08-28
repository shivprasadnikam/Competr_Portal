package com.example.competr.portal.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateScoreRequest {
    private List<Integer> scores;
    private Integer currentSet;
    private String gameStatus;
    private Integer serving;
    private Integer servesLeft;
    private Boolean isMatchEnded;
}

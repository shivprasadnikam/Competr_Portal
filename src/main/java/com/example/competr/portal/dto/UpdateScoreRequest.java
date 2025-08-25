package com.example.competr.portal.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateScoreRequest {
    private List<Integer> scores;
    private Integer serving;
    private Integer servesLeft;
}

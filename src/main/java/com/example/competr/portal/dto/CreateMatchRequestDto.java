package com.example.competr.portal.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateMatchRequestDto {
    private List<String> playerIds;
    private String matchType;
}

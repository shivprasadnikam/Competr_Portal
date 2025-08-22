package com.example.competr.portal.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String errorMessage;
    private boolean status;
    private String token;
    private String userId;
}

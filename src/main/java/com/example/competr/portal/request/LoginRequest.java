package com.example.competr.portal.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String mobile;
    private String password;
}

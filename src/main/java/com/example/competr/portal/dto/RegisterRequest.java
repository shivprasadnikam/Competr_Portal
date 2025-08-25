package com.example.competr.portal.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;
    private String password;
    private String userName;
}

package com.example.competr.portal.request;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String userName;
}

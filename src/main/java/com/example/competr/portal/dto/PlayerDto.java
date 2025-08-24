package com.example.competr.portal.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlayerDto {
    private String id;

    private String userName;
    private String password;

    private String name;

    private Integer age;

    private String nationality;

    private Integer ranking;

    private String gender;

    private String club;

    private LocalDate dateOfBirth;

    private String email;

    private String phoneNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

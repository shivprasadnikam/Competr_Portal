package com.example.competr.portal.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "players")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String userName;
    @Column(unique = true, nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    private Integer age;

    @Column(length = 50)
    private String nationality;

    private Integer ranking;

    @Column(length = 10)
    private String gender;

    @Column(length = 100)
    private String club;

    private LocalDate dateOfBirth;

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();


    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

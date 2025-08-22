package com.example.competr.portal.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;

    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String userName;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}



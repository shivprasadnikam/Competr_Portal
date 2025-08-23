package com.example.competr.portal.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "profiles")
public class Profile {

    @Id
    private String id;
    private long userId;
    private String userName;
    private int ranking;
    private int points;
}


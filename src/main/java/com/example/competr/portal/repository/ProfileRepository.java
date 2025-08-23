package com.example.competr.portal.repository;

import com.example.competr.portal.entity.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
    Profile findByUserId(long userId);
}


package com.example.competr.portal.service;

import com.example.competr.portal.entity.Profile;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {
    public Profile createOrUpdateProfile(Profile profile);
    public Profile getProfileByUserId(long userId);
}

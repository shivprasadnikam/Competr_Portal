package com.example.competr.portal.serviceImpl;

import com.example.competr.portal.entity.Profile;
import com.example.competr.portal.repository.ProfileRepository;
import com.example.competr.portal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public Profile createOrUpdateProfile(Profile profile) {
        Profile existingProfile = profileRepository.findByUserId(profile.getUserId());
        if (existingProfile != null) {
            existingProfile.setUserName(profile.getUserName());
            existingProfile.setRanking(profile.getRanking());
            existingProfile.setPoints(profile.getPoints());
            return profileRepository.save(existingProfile);
        }
        return profileRepository.save(profile);
    }

    public Profile getProfileByUserId(long userId) {
        return profileRepository.findByUserId(userId);
    }
}


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
        Profile existingProfile = profileRepository.findByUserName(profile.getUserName());
        if (existingProfile != null) {
            existingProfile.setUserName(profile.getUserName());
            existingProfile.setRanking(profile.getRanking());
            existingProfile.setPoints(profile.getPoints());
            return profileRepository.save(existingProfile);
        }
        return profileRepository.save(profile);
    }

    public Profile getProfileByUserName(String userName){
        return profileRepository.findByUserName(userName);
    }
}


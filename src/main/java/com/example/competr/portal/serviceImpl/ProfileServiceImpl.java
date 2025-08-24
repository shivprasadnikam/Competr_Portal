//package com.example.competr.portal.serviceImpl;
//
//import com.example.competr.portal.dto.PlayerDto;
//import com.example.competr.portal.entity.PlayerEntity;
//import com.example.competr.portal.service.ProfileService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ProfileServiceImpl implements ProfileService {
//
//    private final ProfileRepository profileRepository;
//
//    public PlayerDto createOrUpdateProfile(PlayerDto profile) {
//        PlayerEntity existingProfile = profileRepository.findByUserName(profile.getUserName());
//        if (existingProfile != null) {
//            existingProfile.setUserName(profile.getUserName());
//            existingProfile.setRanking(profile.getRanking());
//            existingProfile.setPoints(profile.getPoints());
//            return profileRepository.save(existingProfile);
//        }
//        return profileRepository.save(profile);
//    }
//
//    public PlayerDto getProfileByUserName(String userName){
//        return profileRepository.findByUserName(userName);
//    }
//}
//

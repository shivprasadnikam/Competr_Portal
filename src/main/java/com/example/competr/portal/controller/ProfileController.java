//package com.example.competr.portal.controller;
//
//import com.example.competr.portal.dto.PlayerDto;
//import com.example.competr.portal.service.ProfileService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/profiles")
//@RequiredArgsConstructor
//@Slf4j
//public class ProfileController {
//
//    private final ProfileService profileService;
//
////    @PostMapping
////    public ResponseEntity<Profile> createOrUpdateProfile(@RequestBody Profile profile) {
////        Profile savedProfile = profileService.createOrUpdateProfile(profile);
////        return ResponseEntity.ok(savedProfile);
////    }
//    @PostMapping("/userName")
//    public ResponseEntity<PlayerDto> getProfileByUserName(@RequestBody PlayerDto playerDto) {
//        log.info("Inside getProfileByUserName with userName: {}", playerDto.getUserName());
//        PlayerDto profile = profileService.getProfileByUserName(playerDto.getUserName());
//        log.info("Profile found: {}", profile != null);
//        if (profile != null) {
//            return ResponseEntity.ok(profile);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//}
//

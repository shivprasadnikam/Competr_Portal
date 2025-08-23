package com.example.competr.portal.controller;

import com.example.competr.portal.entity.Profile;
import com.example.competr.portal.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<Profile> createOrUpdateProfile(@RequestBody Profile profile) {
        Profile savedProfile = profileService.createOrUpdateProfile(profile);
        return ResponseEntity.ok(savedProfile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Profile> getProfileByUserId(@PathVariable long userId) {
        Profile profile = profileService.getProfileByUserId(userId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


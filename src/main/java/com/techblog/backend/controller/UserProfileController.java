package com.techblog.backend.controller;

import com.techblog.backend.entity.User;
import com.techblog.backend.entity.UserProfile;
import com.techblog.backend.repository.UserProfileRepository;
import com.techblog.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return userProfileRepository.save(newProfile);
                });
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody UserProfile updatedProfile) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return userProfileRepository.save(newProfile);
                });

        profile.setBio(updatedProfile.getBio());
        profile.setWebsite(updatedProfile.getWebsite());
        profile.setLocation(updatedProfile.getLocation());
        profile.setCompany(updatedProfile.getCompany());
        profile.setGithubUsername(updatedProfile.getGithubUsername());
        profile.setTwitterUsername(updatedProfile.getTwitterUsername());

        if (updatedProfile.getAvatarUrl() != null) {
            profile.setAvatarUrl(updatedProfile.getAvatarUrl());
        }

        return ResponseEntity.ok(userProfileRepository.save(profile));
    }
}
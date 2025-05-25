package id.ac.ui.cs.advprog.gatherlove.profile.service;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileRequest;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import id.ac.ui.cs.advprog.gatherlove.profile.repository.ProfileRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public ProfileResponse completeProfile(ProfileRequest request, UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setBio(request.getBio());

        Profile savedProfile = profileRepository.save(profile);

        return ProfileResponse.builder()
                .id(savedProfile.getId())
                .fullName(savedProfile.getFullName())
                .phoneNumber(savedProfile.getPhoneNumber())
                .bio(savedProfile.getBio())
                .build();
    }

    public ProfileResponse viewProfile(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return ProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .phoneNumber(profile.getPhoneNumber())
                .bio(profile.getBio())
                .build();
    }

    public ProfileResponse updateProfile(UUID profileId, ProfileRequest request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setBio(request.getBio());

        Profile updatedProfile = profileRepository.save(profile);

        return ProfileResponse.builder()
                .id(updatedProfile.getId())
                .fullName(updatedProfile.getFullName())
                .phoneNumber(updatedProfile.getPhoneNumber())
                .bio(updatedProfile.getBio())
                .build();
    }

    public void deleteBio(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profile.setBio(null);

        profileRepository.save(profile);
    }

    @Async
    public CompletableFuture<ProfileResponse> completeProfileAsync(ProfileRequest request, UUID userId) {
        ProfileResponse response = completeProfile(request, userId);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<ProfileResponse> updateProfileAsync(UUID profileId, ProfileRequest request) {
        ProfileResponse response = updateProfile(profileId, request);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<Void> deleteBioAsync(UUID profileId) {
        deleteBio(profileId);
        return CompletableFuture.completedFuture(null);
    }
}
package id.ac.ui.cs.advprog.gatherlove.profile.service;

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
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProfileResponse completeProfile(ProfileRequest request, UUID profileId) {
        Profile profile = findProfileById(profileId);
        updateProfileFromRequest(profile, request);
        Profile savedProfile = profileRepository.save(profile);
        return buildProfileResponse(savedProfile);
    }

    @Override
    public ProfileResponse viewProfile(UUID profileId) {
        Profile profile = findProfileById(profileId);
        return buildProfileResponse(profile);
    }

    @Override
    public ProfileResponse updateProfile(UUID profileId, ProfileRequest request) {
        Profile profile = findProfileById(profileId);
        updateProfileFromRequest(profile, request);
        Profile updatedProfile = profileRepository.save(profile);
        return buildProfileResponse(updatedProfile);
    }

    @Override
    public void deleteBio(UUID profileId) {
        Profile profile = findProfileById(profileId);
        profile.setBio(null);
        profileRepository.save(profile);
    }

    @Override
    @Async
    public CompletableFuture<ProfileResponse> completeProfileAsync(ProfileRequest request, UUID userId) {
        ProfileResponse response = completeProfile(request, userId);
        return CompletableFuture.completedFuture(response);
    }

    @Override
    @Async
    public CompletableFuture<ProfileResponse> updateProfileAsync(UUID profileId, ProfileRequest request) {
        ProfileResponse response = updateProfile(profileId, request);
        return CompletableFuture.completedFuture(response);
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteBioAsync(UUID profileId) {
        deleteBio(profileId);
        return CompletableFuture.completedFuture(null);
    }

    // Helper methods to reduce code duplication
    private Profile findProfileById(UUID profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private void updateProfileFromRequest(Profile profile, ProfileRequest request) {
        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setBio(request.getBio());
    }

    private ProfileResponse buildProfileResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .phoneNumber(profile.getPhoneNumber())
                .bio(profile.getBio())
                .build();
    }
}

package id.ac.ui.cs.advprog.gatherlove.profile.service;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileRequest;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import id.ac.ui.cs.advprog.gatherlove.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public ProfileResponse completeProfile(ProfileRequest request, UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Profile profile = Profile.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .bio(request.getBio())
                .user(user)
                .build();

        Profile savedProfile = profileRepository.save(profile);

        return ProfileResponse.builder()
                .id(savedProfile.getId())
                .name(savedProfile.getName())
                .phoneNumber(savedProfile.getPhoneNumber())
                .bio(savedProfile.getBio())
                .build();
    }

    public ProfileResponse viewProfile(UUID profileId) {  // Changed from Long to UUID
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return ProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .phoneNumber(profile.getPhoneNumber())
                .bio(profile.getBio())
                .build();
    }

    public ProfileResponse updateProfile(UUID profileId, ProfileRequest request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profile.setName(request.getName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setBio(request.getBio());

        Profile updatedProfile = profileRepository.save(profile);

        return ProfileResponse.builder()
                .id(updatedProfile.getId())
                .name(updatedProfile.getName())
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
}
package id.ac.ui.cs.advprog.gatherlove.profile.service;

import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileRequest;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProfileService {
    ProfileResponse completeProfile(ProfileRequest request, UUID profileId);
    
    ProfileResponse viewProfile(UUID profileId);
    
    ProfileResponse updateProfile(UUID profileId, ProfileRequest request);
    
    void deleteBio(UUID profileId);
    
    CompletableFuture<ProfileResponse> completeProfileAsync(ProfileRequest request, UUID userId);
    
    CompletableFuture<ProfileResponse> updateProfileAsync(UUID profileId, ProfileRequest request);
    
    CompletableFuture<Void> deleteBioAsync(UUID profileId);
}

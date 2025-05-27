package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.dto.MessageResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileRequest;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<?> completeProfile(
            @Valid @RequestBody ProfileRequest request,
            @PathVariable UUID userId) {

        ResponseEntity<?> validationResponse = validateProfileRequest(request);
        if (validationResponse != null) {
            return validationResponse;
        }
        
        ProfileResponse response = profileService.completeProfile(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}/async")
    public CompletableFuture<ResponseEntity<?>> completeProfileAsync(
            @Valid @RequestBody ProfileRequest request,
            @PathVariable UUID userId) {

        ResponseEntity<?> validationResponse = validateProfileRequest(request);
        if (validationResponse != null) {
            return CompletableFuture.completedFuture(validationResponse);
        }

        return profileService.completeProfileAsync(request, userId)
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> viewProfile(@PathVariable UUID profileId) {
        ProfileResponse response = profileService.viewProfile(profileId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/edit/{profileId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable UUID profileId,
            @Valid @RequestBody ProfileRequest request) {

        ResponseEntity<?> validationResponse = validateProfileRequest(request);
        if (validationResponse != null) {
            return validationResponse;
        }

        ProfileResponse response = profileService.updateProfile(profileId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/edit/{profileId}/async")
    public CompletableFuture<ResponseEntity<?>> updateProfileAsync(
            @PathVariable UUID profileId,
            @Valid @RequestBody ProfileRequest request) {

        ResponseEntity<?> validationResponse = validateProfileRequest(request);
        if (validationResponse != null) {
            return CompletableFuture.completedFuture(validationResponse);
        }

        return profileService.updateProfileAsync(profileId, request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{profileId}/bio")
    public ResponseEntity<Void> deleteBio(@PathVariable UUID profileId) {
        profileService.deleteBio(profileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{profileId}/bio/async")
    public CompletableFuture<ResponseEntity<Void>> deleteBioAsync(@PathVariable UUID profileId) {
        return profileService.deleteBioAsync(profileId)
                .thenApply(result -> ResponseEntity.noContent().build());
    }
    
    private ResponseEntity<?> validateProfileRequest(ProfileRequest request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Phone number is required"));
        }

        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Full name is required"));
        }
        
        return null;
    }
}

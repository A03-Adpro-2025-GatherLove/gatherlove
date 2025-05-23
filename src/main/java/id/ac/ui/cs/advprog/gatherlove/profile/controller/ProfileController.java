package id.ac.ui.cs.advprog.gatherlove.profile.controller;

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
    public ResponseEntity<ProfileResponse> completeProfile(
            @Valid @RequestBody ProfileRequest request,
            @PathVariable UUID userId) {
        ProfileResponse response = profileService.completeProfile(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}")
    public CompletableFuture<ResponseEntity<ProfileResponse>> completeProfileAsync(
            @Valid @RequestBody ProfileRequest request,
            @PathVariable UUID userId) {
        return profileService.completeProfileAsync(request, userId)
                .thenApply(response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> viewProfile(@PathVariable UUID profileId) {  // Changed from Long to UUID
        ProfileResponse response = profileService.viewProfile(profileId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable UUID profileId,  // Changed from Long to UUID
            @Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.updateProfile(profileId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{profileId}")
    public CompletableFuture<ResponseEntity<ProfileResponse>> updateProfileAsync(
            @PathVariable UUID profileId,
            @Valid @RequestBody ProfileRequest request) {
        return profileService.updateProfileAsync(profileId, request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{profileId}/bio")
    public ResponseEntity<Void> deleteBio(@PathVariable UUID profileId) {  // Changed from Long to UUID
        profileService.deleteBio(profileId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{profileId}/bio")
    public CompletableFuture<ResponseEntity<Void>> deleteBioAsync(@PathVariable UUID profileId) {
        return profileService.deleteBioAsync(profileId)
                .thenApply(result -> ResponseEntity.noContent().build());
    }
}

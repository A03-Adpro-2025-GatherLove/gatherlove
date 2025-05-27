package id.ac.ui.cs.advprog.gatherlove.profile.service;

import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileRequest;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import id.ac.ui.cs.advprog.gatherlove.profile.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private UUID profileId;
    private Profile profile;
    private ProfileRequest profileRequest;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        
        profile = new Profile();
        profile.setId(profileId);
        profile.setFullName("John Doe");
        profile.setPhoneNumber("1234567890");
        profile.setBio("Software Developer");

        profileRequest = new ProfileRequest();
        profileRequest.setFullName("John Doe");
        profileRequest.setPhoneNumber("1234567890");
        profileRequest.setBio("Software Developer");
    }

    @Test
    void completeProfile_ShouldReturnProfileResponse() {
        // Arrange
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        ProfileResponse response = profileService.completeProfile(profileRequest, profileId);

        // Assert
        assertNotNull(response);
        assertEquals(profileId, response.getId());
        assertEquals(profileRequest.getFullName(), response.getFullName());
        assertEquals(profileRequest.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(profileRequest.getBio(), response.getBio());
        
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void viewProfile_ShouldReturnProfileResponse() {
        // Arrange
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Act
        ProfileResponse response = profileService.viewProfile(profileId);

        // Assert
        assertNotNull(response);
        assertEquals(profileId, response.getId());
        assertEquals(profile.getFullName(), response.getFullName());
        assertEquals(profile.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(profile.getBio(), response.getBio());
        
        verify(profileRepository).findById(profileId);
    }

    @Test
    void viewProfile_WhenProfileNotFound_ShouldThrowException() {
        // Arrange
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            profileService.viewProfile(profileId);
        });
        
        verify(profileRepository).findById(profileId);
    }

    @Test
    void updateProfile_ShouldReturnUpdatedProfile() {
        // Arrange
        ProfileRequest updateRequest = new ProfileRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setPhoneNumber("9876543210");
        updateRequest.setBio("Updated Bio");

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        ProfileResponse response = profileService.updateProfile(profileId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(profileId, response.getId());
        assertEquals(updateRequest.getFullName(), response.getFullName());
        assertEquals(updateRequest.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(updateRequest.getBio(), response.getBio());
        
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void deleteBio_ShouldRemoveBio() {
        // Arrange
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        profileService.deleteBio(profileId);

        // Assert
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(profile);
        assertNull(profile.getBio());
    }

    @Test
    void completeProfileAsync_ShouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Arrange
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        CompletableFuture<ProfileResponse> future = profileService.completeProfileAsync(profileRequest, profileId);

        // Assert
        assertNotNull(future);
        ProfileResponse response = future.get();
        assertEquals(profileId, response.getId());
        assertEquals(profileRequest.getFullName(), response.getFullName());
        
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void updateProfileAsync_ShouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Arrange
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        CompletableFuture<ProfileResponse> future = profileService.updateProfileAsync(profileId, profileRequest);

        // Assert
        assertNotNull(future);
        ProfileResponse response = future.get();
        assertEquals(profileId, response.getId());
        assertEquals(profileRequest.getFullName(), response.getFullName());
        
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void deleteBioAsync_ShouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Arrange
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        // Act
        CompletableFuture<Void> future = profileService.deleteBioAsync(profileId);

        // Assert
        assertNotNull(future);
        future.get(); // Wait for completion
        
        verify(profileRepository).findById(profileId);
        verify(profileRepository).save(profile);
        assertNull(profile.getBio());
    }
}

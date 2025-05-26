package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.dto.MessageResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileRequest;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private UUID userId;
    private UUID profileId;
    private ProfileRequest validRequest;
    private ProfileRequest invalidRequest;
    private ProfileResponse mockResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        
        validRequest = new ProfileRequest();
        validRequest.setFullName("Test User");
        validRequest.setPhoneNumber("123456789");
        validRequest.setBio("Test bio");
        
        invalidRequest = new ProfileRequest();
        
        mockResponse = new ProfileResponse();
        mockResponse.setId(profileId);
        mockResponse.setFullName("Test User");
        mockResponse.setPhoneNumber("123456789");
        mockResponse.setBio("Test bio");
    }

    @Test
    void completeProfile_WithValidRequest_ReturnsCreatedStatus() {
        // Arrange
        when(profileService.completeProfile(any(ProfileRequest.class), any(UUID.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = profileController.completeProfile(validRequest, userId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(profileService).completeProfile(validRequest, userId);
    }

    @Test
    void completeProfile_WithEmptyPhoneNumber_ReturnsBadRequest() {
        // Arrange
        ProfileRequest request = new ProfileRequest();
        request.setFullName("Test User");
        
        // Act
        ResponseEntity<?> response = profileController.completeProfile(request, userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Error: Phone number is required", ((MessageResponse) response.getBody()).getMessage());
        verify(profileService, never()).completeProfile(any(), any());
    }

    @Test
    void completeProfile_WithEmptyFullName_ReturnsBadRequest() {
        // Arrange
        ProfileRequest request = new ProfileRequest();
        request.setPhoneNumber("123456789");
        
        // Act
        ResponseEntity<?> response = profileController.completeProfile(request, userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Error: Full name is required", ((MessageResponse) response.getBody()).getMessage());
        verify(profileService, never()).completeProfile(any(), any());
    }

    @Test
    void completeProfileAsync_WithValidRequest_ReturnsCreatedStatus() {
        // Arrange
        when(profileService.completeProfileAsync(any(ProfileRequest.class), any(UUID.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act
        CompletableFuture<ResponseEntity<?>> futureResponse = profileController.completeProfileAsync(validRequest, userId);
        ResponseEntity<?> response = futureResponse.join();

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(profileService).completeProfileAsync(validRequest, userId);
    }

    @Test
    void viewProfile_ReturnsProfile() {
        // Arrange
        when(profileService.viewProfile(profileId)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ProfileResponse> response = profileController.viewProfile(profileId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(profileService).viewProfile(profileId);
    }

    @Test
    void updateProfile_WithValidRequest_ReturnsOkStatus() {
        // Arrange
        when(profileService.updateProfile(eq(profileId), any(ProfileRequest.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = profileController.updateProfile(profileId, validRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(profileService).updateProfile(profileId, validRequest);
    }

    @Test
    void updateProfileAsync_WithValidRequest_ReturnsOkStatus() {
        // Arrange
        when(profileService.updateProfileAsync(eq(profileId), any(ProfileRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act
        CompletableFuture<ResponseEntity<?>> futureResponse = profileController.updateProfileAsync(profileId, validRequest);
        ResponseEntity<?> response = futureResponse.join();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(profileService).updateProfileAsync(profileId, validRequest);
    }

    @Test
    void deleteBio_ReturnsNoContent() {
        // Arrange
        doNothing().when(profileService).deleteBio(profileId);

        // Act
        ResponseEntity<Void> response = profileController.deleteBio(profileId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(profileService).deleteBio(profileId);
    }

    @Test
    void deleteBioAsync_ReturnsNoContent() {
        // Arrange
        when(profileService.deleteBioAsync(profileId))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        CompletableFuture<ResponseEntity<Void>> futureResponse = profileController.deleteBioAsync(profileId);
        ResponseEntity<Void> response = futureResponse.join();

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(profileService).deleteBioAsync(profileId);
    }
}

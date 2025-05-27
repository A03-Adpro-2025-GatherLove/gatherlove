package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileRequest;
import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfileService profileService;

    private UUID userId;
    private UUID profileId;
    private ProfileRequest validRequest;
    private ProfileResponse mockResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        
        validRequest = new ProfileRequest();
        validRequest.setFullName("Test User");
        validRequest.setPhoneNumber("123456789");
        validRequest.setBio("Test bio");
        
        mockResponse = new ProfileResponse();
        mockResponse.setId(profileId);
        mockResponse.setFullName("Test User");
        mockResponse.setPhoneNumber("123456789");
        mockResponse.setBio("Test bio");
    }

    @Test
    void completeProfile_WithValidRequest_ShouldReturnCreatedStatus() throws Exception {
        // Arrange
        when(profileService.completeProfile(any(ProfileRequest.class), any(UUID.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/profiles/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(profileId.toString()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.bio").value("Test bio"));
                
        verify(profileService).completeProfile(any(ProfileRequest.class), eq(userId));
    }

    @Test
    void completeProfile_WithMissingPhoneNumber_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ProfileRequest invalidRequest = new ProfileRequest();
        invalidRequest.setFullName("Test User");

        // Act & Assert
        mockMvc.perform(post("/api/profiles/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Phone number is required"));
                
        verify(profileService, never()).completeProfile(any(), any());
    }

    @Test
    void completeProfile_WithMissingFullName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ProfileRequest invalidRequest = new ProfileRequest();
        invalidRequest.setPhoneNumber("123456789");

        // Act & Assert
        mockMvc.perform(post("/api/profiles/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Full name is required"));
                
        verify(profileService, never()).completeProfile(any(), any());
    }

    @Test
    void completeProfileAsync_WithValidRequest_ShouldReturnCreatedStatus() throws Exception {
        // Arrange
        when(profileService.completeProfileAsync(any(ProfileRequest.class), any(UUID.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act & Assert
        mockMvc.perform(post("/api/profiles/users/{userId}/async", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(profileId.toString()));
                
        verify(profileService).completeProfileAsync(any(ProfileRequest.class), eq(userId));
    }

    @Test
    void viewProfile_ShouldReturnProfile() throws Exception {
        // Arrange
        when(profileService.viewProfile(profileId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/profiles/{profileId}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId.toString()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.bio").value("Test bio"));
                
        verify(profileService).viewProfile(profileId);
    }

    @Test
    void updateProfile_WithValidRequest_ShouldReturnOkStatus() throws Exception {
        // Arrange
        when(profileService.updateProfile(eq(profileId), any(ProfileRequest.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(put("/api/profiles/users/edit/{profileId}", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId.toString()));
                
        verify(profileService).updateProfile(eq(profileId), any(ProfileRequest.class));
    }

    @Test
    void updateProfileAsync_WithValidRequest_ShouldReturnOkStatus() throws Exception {
        // Arrange
        when(profileService.updateProfileAsync(eq(profileId), any(ProfileRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        // Act & Assert
        mockMvc.perform(put("/api/profiles/users/edit/{profileId}/async", profileId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId.toString()));
                
        verify(profileService).updateProfileAsync(eq(profileId), any(ProfileRequest.class));
    }

    @Test
    void deleteBio_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(profileService).deleteBio(profileId);

        // Act & Assert
        mockMvc.perform(delete("/api/profiles/{profileId}/bio", profileId))
                .andExpect(status().isNoContent());
                
        verify(profileService).deleteBio(profileId);
    }

    @Test
    void deleteBioAsync_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(profileService.deleteBioAsync(profileId))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act & Assert
        mockMvc.perform(delete("/api/profiles/{profileId}/bio/async", profileId))
                .andExpect(status().isNoContent());
                
        verify(profileService).deleteBioAsync(profileId);
    }
}

package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EditProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private Model model;

    @InjectMocks
    private EditProfileController controller;

    private UUID profileId;
    private ProfileResponse mockProfile;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        mockProfile = new ProfileResponse();
        // Configure mock profile with sample data
        mockProfile.setId(profileId);
        mockProfile.setFullName("Test User");
    }

    @Test
    void testGetEditProfilePageSuccess() {
        // Arrange
        when(profileService.viewProfile(profileId)).thenReturn(mockProfile);
        
        // Act
        String viewName = controller.getEditProfilePage(profileId, model);
        
        // Assert
        assertEquals("profile/edit-profile", viewName);
        verify(model).addAttribute(eq("profile"), eq(mockProfile));
        verify(profileService).viewProfile(profileId);
    }

    @Test
    void testGetEditProfilePageModelAttributes() {
        // Arrange
        when(profileService.viewProfile(any(UUID.class))).thenReturn(mockProfile);
        
        // Act
        controller.getEditProfilePage(profileId, model);
        
        // Assert
        verify(model).addAttribute("profile", mockProfile);
    }
}

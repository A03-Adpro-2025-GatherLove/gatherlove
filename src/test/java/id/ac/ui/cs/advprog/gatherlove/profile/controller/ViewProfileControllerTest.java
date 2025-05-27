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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ViewProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private Model model;

    @InjectMocks
    private ViewProfileController controller;

    private UUID profileId;
    private ProfileResponse mockProfile;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        mockProfile = new ProfileResponse();
        mockProfile.setId(profileId);
        mockProfile.setFullName("Test User");
        mockProfile.setPhoneNumber("1234567890");
        mockProfile.setBio("This is a test bio");
    }

    @Test
    void viewProfile_ShouldAddProfileToModelAndReturnCorrectView() {
        // Arrange
        when(profileService.viewProfile(profileId)).thenReturn(mockProfile);

        // Act
        String viewName = controller.viewProfile(profileId, model);

        // Assert
        assertEquals("profile/view-profile", viewName);
        verify(model).addAttribute(eq("profile"), eq(mockProfile));
        verify(profileService).viewProfile(profileId);
    }
}

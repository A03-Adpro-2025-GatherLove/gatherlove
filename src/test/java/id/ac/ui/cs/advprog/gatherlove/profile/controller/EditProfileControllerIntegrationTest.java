package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EditProfileController.class)
public class EditProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    private UUID profileId;
    private ProfileResponse mockProfile;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        mockProfile = new ProfileResponse();
        mockProfile.setId(profileId);
        mockProfile.setFullName("Test User");
    }

    @Test
    void testGetEditProfilePage() throws Exception {
        // Arrange
        when(profileService.viewProfile(any(UUID.class))).thenReturn(mockProfile);

        // Act & Assert
        mockMvc.perform(get("/profile/edit/" + profileId))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit-profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attribute("profile", mockProfile));
    }

    @Test
    void testGetEditProfilePageInvalidId() throws Exception {
        // Arrange
        when(profileService.viewProfile(any(UUID.class))).thenThrow(new RuntimeException("Profile not found"));

        // Act & Assert
        mockMvc.perform(get("/profile/edit/" + profileId))
                .andExpect(status().isInternalServerError());
    }
}

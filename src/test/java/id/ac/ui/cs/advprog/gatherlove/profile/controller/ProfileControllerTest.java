package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatherlove.profile.model.Profile;
import id.ac.ui.cs.advprog.gatherlove.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    private Profile dummyProfile;

    @BeforeEach
    void setUp() {
        dummyProfile = new Profile();
        dummyProfile.setId(1L);
        dummyProfile.setName("John Doe");
        dummyProfile.setEmail("john@example.com");
        dummyProfile.setPhoneNumber("081234567890");
        dummyProfile.setBio("Hi, I'm John.");
    }

    @Test
    void completeProfile_shouldReturnCreatedProfile() throws Exception {
        Mockito.when(profileService.completeProfile(any(Profile.class))).thenReturn(dummyProfile);

        mockMvc.perform(post("/api/profile/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void viewProfile_shouldReturnProfileById() throws Exception {
        Mockito.when(profileService.viewProfile(1L)).thenReturn(Optional.of(dummyProfile));

        mockMvc.perform(get("/api/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void updateProfile_shouldUpdateAndReturnProfile() throws Exception {
        dummyProfile.setName("Updated Name");
        Mockito.when(profileService.updateProfile(eq(1L), any(Profile.class))).thenReturn(dummyProfile);

        mockMvc.perform(put("/api/profile/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteBio_shouldClearBioField() throws Exception {
        dummyProfile.setBio(null);
        Mockito.when(profileService.deleteBio(1L)).thenReturn(dummyProfile);

        mockMvc.perform(delete("/api/profile/1/bio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").doesNotExist());
    }
}

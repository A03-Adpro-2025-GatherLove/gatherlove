package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompleteProfileController.class)
public class CompleteProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void testGetCompleteProfilePage() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/profile/complete/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/complete-profile"))
                .andExpect(model().attributeExists("id"))
                .andExpect(model().attribute("id", userId));
    }

    @Test
    void testGetCompleteProfilePageWithInvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/profile/complete/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}

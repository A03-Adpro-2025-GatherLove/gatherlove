package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.*; // Import exceptions

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser; // For testing security
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // For CSRF


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*; // For jsonPath checks

@WebMvcTest(DonationController.class)
// @Import(SecurityConfig.class) // Import security config if needed for testing
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DonationService donationService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    private UUID campaignId;
    private UUID donorId; // Assume this comes from security context
    private UUID donationId;
    private Donation donation;
    private DonationResponse donationResponse;
    private CreateDonationRequest createRequest;

    // Use a fixed UUID for the mocked user if needed, or extract from @WithMockUser principal
    private final String MOCK_USER_ID_STR = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11";
    private final UUID MOCK_USER_ID = UUID.fromString(MOCK_USER_ID_STR);


    @BeforeEach
    void setUp() {
        campaignId = UUID.randomUUID();
        donorId = MOCK_USER_ID; // Use consistent mock user ID
        donationId = UUID.randomUUID();

        donation = Donation.builder()
                .id(donationId)
                .campaignId(campaignId)
                .donorId(donorId)
                .amount(new BigDecimal("150.00"))
                .message("Test from controller")
                .donationTimestamp(LocalDateTime.now())
                .build();

        donationResponse = DonationResponse.builder()
                .id(donationId)
                .campaignId(campaignId)
                .donorId(donorId)
                .amount(new BigDecimal("150.00"))
                .message("Test from controller")
                .donationTimestamp(donation.getDonationTimestamp())
                .build();

        createRequest = new CreateDonationRequest();
        createRequest.setCampaignId(campaignId);
        createRequest.setAmount(new BigDecimal("150.00"));
        createRequest.setMessage("Test from controller");
    }

    // Helper to mock the controller's getCurrentUserId() behavior if needed
    // Or rely on @WithMockUser to populate SecurityContextHolder

    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"}, // Mock a logged-in user
            userId = MOCK_USER_ID_STR) // Custom attribute if your UserDetails stores UUID as string
    void makeDonation_whenValidRequest_shouldReturnCreated() throws Exception {
        // Arrange
        when(donationService.createDonation(eq(donorId), eq(campaignId), any(BigDecimal.class), anyString()))
                .thenReturn(donation);

        // Act & Assert
        mockMvc.perform(post("/api/donations")
                        .with(csrf()) // Add CSRF token for POST/PUT/DELETE if enabled
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(donationId.toString())))
                .andExpect(jsonPath("$.donorId", is(donorId.toString())))
                .andExpect(jsonPath("$.amount", is(150.00)))
                .andExpect(jsonPath("$.message", is("Test from controller")));

        verify(donationService, times(1)).createDonation(eq(donorId), eq(campaignId), eq(createRequest.getAmount()), eq(createRequest.getMessage()));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"}, userId = MOCK_USER_ID_STR)
    void makeDonation_whenInvalidAmount_shouldReturnBadRequest() throws Exception {
        // Arrange
        createRequest.setAmount(BigDecimal.ZERO); // Invalid amount

        // Act & Assert
        mockMvc.perform(post("/api/donations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest()); // Expect 400 due to validation

        verify(donationService, never()).createDonation(any(), any(), any(), any()); // Service should not be called
    }


    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"}, userId = MOCK_USER_ID_STR)
    void removeMessage_whenOwnerRequests_shouldReturnOk() throws Exception {
        // Arrange
        Donation updatedDonation = Donation.builder()
                .id(donationId)
                .campaignId(campaignId)
                .donorId(donorId)
                .amount(new BigDecimal("150.00"))
                .message(null) // Message removed
                .donationTimestamp(donation.getDonationTimestamp())
                .build();
        when(donationService.removeDonationMessage(eq(donationId), eq(donorId))).thenReturn(updatedDonation);

        // Act & Assert
        mockMvc.perform(delete("/api/donations/{donationId}/message", donationId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(donationId.toString())))
                .andExpect(jsonPath("$.message").doesNotExist()); // Check message is null/absent

        verify(donationService, times(1)).removeDonationMessage(donationId, donorId);
    }

    @Test
    @WithMockUser(username = "otheruser", authorities = {"USER"}, userId = "b1ffcafe-9c0b-4ef8-bb6d-6bb9bd380a22") // Different user
    void removeMessage_whenNotOwner_shouldReturnForbidden() throws Exception {
        // Arrange
        // Simulate the service throwing UnauthorizedException
        when(donationService.removeDonationMessage(eq(donationId), any(UUID.class))) // Use any() for user ID here as it will be different
                .thenThrow(new UnauthorizedException("User not authorized"));


        // Act & Assert
        mockMvc.perform(delete("/api/donations/{donationId}/message", donationId)
                        .with(csrf()))
                .andExpect(status().isForbidden()); // Or isUnauthorized depending on exception mapping

        verify(donationService, times(1)).removeDonationMessage(eq(donationId), any(UUID.class));
    }


    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"}, userId = MOCK_USER_ID_STR)
    void getMyDonationHistory_shouldReturnUserDonations() throws Exception {
        // Arrange
        List<Donation> donations = Arrays.asList(donation);
        when(donationService.findDonationsByDonor(donorId)).thenReturn(donations);

        // Act & Assert
        mockMvc.perform(get("/api/donations/my-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(donationId.toString())))
                .andExpect(jsonPath("$[0].donorId", is(donorId.toString())));

        verify(donationService, times(1)).findDonationsByDonor(donorId);
    }

    @Test
        // No specific user needed usually, public data
    void getDonationsForCampaign_shouldReturnCampaignDonations() throws Exception {
        // Arrange
        Donation anotherDonation = Donation.builder().id(UUID.randomUUID()).campaignId(campaignId).donorId(UUID.randomUUID()).amount(BigDecimal.TEN).build();
        List<Donation> donations = Arrays.asList(donation, anotherDonation);
        when(donationService.findDonationsByCampaign(campaignId)).thenReturn(donations);

        // Act & Assert
        mockMvc.perform(get("/api/donations/campaign/{campaignId}", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].campaignId", is(campaignId.toString())))
                .andExpect(jsonPath("$[1].campaignId", is(campaignId.toString())));

        verify(donationService, times(1)).findDonationsByCampaign(campaignId);
    }

    // Add tests for Admin endpoint if implemented, using @WithMockUser(roles = {"ADMIN"})
}
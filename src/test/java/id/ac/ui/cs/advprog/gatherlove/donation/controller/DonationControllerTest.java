package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(DonationController.class)
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DonationService donationService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String MOCK_USERNAME = "testuser";
    private final Long MOCK_USER_ID = 1L;
    private UUID campaignId;
    private UUID donationId;
    private Donation donation;

    @BeforeEach
    void setUp() {
        campaignId = UUID.randomUUID();
        donationId = UUID.randomUUID();

        donation = Donation.builder()
                .id(donationId)
                .campaignId(campaignId)
                .donorId(MOCK_USER_ID)
                .amount(new BigDecimal("100.00"))
                .message("msg")
                .build();
    }

    @Test
    @WithUserDetails(value = "testuser", userDetailsServiceBeanName = "customUserDetailsService")
    void authIntegration_makeDonation_shouldInjectUserId() throws Exception {
        CreateDonationRequest req = new CreateDonationRequest();
        req.setCampaignId(campaignId);
        req.setAmount(new BigDecimal("100.00"));
        req.setMessage("msg");

        when(donationService.createDonation(eq(MOCK_USER_ID), any(UUID.class), any(BigDecimal.class), anyString()))
                .thenReturn(donation);

        mockMvc.perform(post("/api/donations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.donorId", is(MOCK_USER_ID.intValue())));
    }
}
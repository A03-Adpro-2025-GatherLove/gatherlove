package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.donation.command.CommandInvoker;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.DonationNotFoundException;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.UnauthorizedException;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;


@WebMvcTest(DonationController.class) // Hanya test DonationController, mock dependencies lain
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mock Spring beans
    private DonationService donationService;

    @MockBean
    private CommandInvoker commandInvoker;

    @Autowired
    private ObjectMapper objectMapper; // Untuk konversi objek ke JSON

    private UserDetailsImpl mockUserDetails;
    private UUID userId;
    private Donation donation;
    private DonationResponse donationResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        // Membuat mock UserDetailsImpl
        // Anda mungkin perlu menyesuaikan konstruktor UserDetailsImpl Anda
        mockUserDetails = new UserDetailsImpl(userId, "testuser", "test@example.com", "password", Collections.emptyList(), null /* UserEntity */);


        donation = Donation.builder()
                .id(UUID.randomUUID())
                .donorId(userId)
                .campaignId("campaign-1")
                .amount(new BigDecimal("100.00"))
                .message("Test")
                .donationTimestamp(LocalDateTime.now())
                .build();

        donationResponse = DonationResponse.from(donation);
    }

    private Authentication getAuthentication() {
        return new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());
    }


    @Test
    void makeDonation_authenticatedUser_returnsCreated() throws Exception {
        CreateDonationRequest request = new CreateDonationRequest();
        request.setCampaignId("campaign-1");
        request.setAmount(new BigDecimal("100.00"));
        request.setMessage("Test");

        when(commandInvoker.executeCommand(any(MakeDonationCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(donation));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/donations")
                        .with(authentication(getAuthentication())) // Menggunakan mock authentication
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted()) // Karena mengembalikan CompletableFuture
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(donation.getId().toString()))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(commandInvoker, times(1)).executeCommand(any(MakeDonationCommand.class));
    }

    @Test
    void makeDonation_unauthenticatedUser_returnsUnauthorized() throws Exception {
        CreateDonationRequest request = new CreateDonationRequest();
        // ... set request properties ...

        mockMvc.perform(MockMvcRequestBuilders.post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Tidak perlu .andExpect(request().asyncStarted()) karena langsung return completedFuture
                .andExpect(status().isUnauthorized());

        verify(commandInvoker, never()).executeCommand(any());
    }

    @Test
    void makeDonation_commandFailsWithIllegalArgument_returnsBadRequest() throws Exception {
        CreateDonationRequest request = new CreateDonationRequest();
        request.setCampaignId("campaign-1");
        request.setAmount(new BigDecimal("100.00"));

        when(commandInvoker.executeCommand(any(MakeDonationCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalArgumentException("Bad amount")));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/donations")
                        .with(authentication(getAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }


    @Test
    void removeMessage_authenticatedUserAndOwner_returnsOk() throws Exception {
        UUID donationId = donation.getId();
        when(commandInvoker.executeCommand(any(RemoveDonationMessageCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(donation)); // Donation dengan message null

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/donations/{id}/message", donationId)
                        .with(authentication(getAuthentication())))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(donationId.toString()));

        verify(commandInvoker, times(1)).executeCommand(any(RemoveDonationMessageCommand.class));
    }

    @Test
    void removeMessage_commandFailsWithUnauthorized_returnsForbidden() throws Exception {
        UUID donationId = donation.getId();
        when(commandInvoker.executeCommand(any(RemoveDonationMessageCommand.class)))
                .thenReturn(CompletableFuture.failedFuture(new UnauthorizedException("Not owner")));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/donations/{id}/message", donationId)
                        .with(authentication(getAuthentication())))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyHistory_authenticatedUser_returnsOkWithDonations() throws Exception {
        List<Donation> donations = Arrays.asList(donation);
        when(donationService.findDonationsByDonor(userId)).thenReturn(donations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/donations/my-history")
                        .with(authentication(getAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(donation.getId().toString()));

        verify(donationService, times(1)).findDonationsByDonor(userId);
    }

    @Test
    void getByCampaign_returnsOkWithDonations() throws Exception {
        String campaignId = "campaign-1";
        List<Donation> donations = Arrays.asList(donation);
        when(donationService.findDonationsByCampaign(campaignId)).thenReturn(donations);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/donations/campaign/{campaignId}", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(donation.getId().toString()));

        verify(donationService, times(1)).findDonationsByCampaign(campaignId);
    }
}
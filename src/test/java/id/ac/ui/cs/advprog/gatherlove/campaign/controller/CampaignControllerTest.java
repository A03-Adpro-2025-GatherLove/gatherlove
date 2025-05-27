package id.ac.ui.cs.advprog.gatherlove.campaign.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignControllerTest {

    @Mock
    private CampaignService campaignService;

    @Mock
    private DonationService donationService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetailsImpl userDetails;

    @InjectMocks
    private CampaignController campaignController;

    private UserEntity testUser;
    private Campaign testCampaign;
    private CampaignDto testCampaignDto;
    private List<Donation> testDonations;
    private UserEntity otherUser;
    private String campaignId;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new UserEntity();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        // Setup another user for authorization tests
        otherUser = new UserEntity();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        
        // Setup user details
        when(userDetails.getUser()).thenReturn(testUser);
        
        // Initialize campaign ID
        campaignId = UUID.randomUUID().toString();
        
        // Setup test campaign
        testCampaign = Campaign.builder()
                .id(campaignId)
                .title("Test Campaign")
                .description("This is a test campaign")
                .targetAmount(new BigDecimal("1000"))
                .deadline(LocalDate.now().plusDays(30))
                .imageUrl("https://example.com/image.jpg")
                .fundraiser(testUser)
                .status(CampaignStatus.APPROVED)
                .totalDonated(new BigDecimal("250"))
                .withdrawn(false)
                .createdAt(LocalDateTime.now())
                .build();
                
        // Setup test campaign DTO
        testCampaignDto = new CampaignDto();
        testCampaignDto.setTitle("Test Campaign");
        testCampaignDto.setDescription("This is a test campaign");
        testCampaignDto.setTargetAmount(new BigDecimal("1000"));
        testCampaignDto.setDeadline(LocalDate.now().plusDays(30));
        testCampaignDto.setImageUrl("https://example.com/image.jpg");
        
        // Setup test donations
        testDonations = new ArrayList<>();
        Donation donation1 = new Donation();
        donation1.setAmount(new BigDecimal("100"));
        donation1.setDonorId(UUID.randomUUID());
        testDonations.add(donation1);
        
        Donation donation2 = new Donation();
        donation2.setAmount(new BigDecimal("150"));
        donation2.setDonorId(UUID.randomUUID());
        testDonations.add(donation2);
    }

    @Test
    void testCreateCampaign_Success() {
        // Given
        // Create specific test data for better traceability
        String expectedTitle = "Charity Fundraiser 2025";
        String expectedDescription = "Help us raise funds for children's education";
        BigDecimal expectedAmount = new BigDecimal("5000.50");
        LocalDate expectedDeadline = LocalDate.now().plusMonths(3);
        String expectedImageUrl = "https://example.com/fundraiser-image.jpg";
        String expectedSuccessMessage = "Kampanye berhasil dibuat!";
        
        // Prepare the campaign DTO with specific test data
        testCampaignDto.setTitle(expectedTitle);
        testCampaignDto.setDescription(expectedDescription);
        testCampaignDto.setTargetAmount(expectedAmount);
        testCampaignDto.setDeadline(expectedDeadline);
        testCampaignDto.setImageUrl(expectedImageUrl);
        
        // Configure mocks to return our controlled Campaign object
        Campaign createdCampaign = Campaign.builder()
                .id(UUID.randomUUID().toString())
                .title(expectedTitle)
                .description(expectedDescription)
                .targetAmount(expectedAmount)
                .deadline(expectedDeadline)
                .imageUrl(expectedImageUrl)
                .fundraiser(testUser)
                .status(CampaignStatus.PENDING_VERIFICATION) // Initial status should be pending verification
                .totalDonated(BigDecimal.ZERO)
                .withdrawn(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        when(bindingResult.hasErrors()).thenReturn(false);
        when(campaignService.createCampaign(any(CampaignDto.class), any(UserEntity.class))).thenReturn(createdCampaign);
        // Capture the flash attribute to verify its exact content
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String value = invocation.getArgument(1);
            assertEquals("successMessage", key);
            assertEquals(expectedSuccessMessage, value);
            return null;
        }).when(redirectAttributes).addFlashAttribute(anyString(), anyString());

        // When
        String viewName = campaignController.createCampaign(testCampaignDto, bindingResult, userDetails, model, redirectAttributes);

        // Then
        // Verify exact campaign creation parameters
        verify(campaignService).createCampaign(eq(testCampaignDto), eq(testUser));
        
        // Verify that success message was added with exact expected content
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), eq(expectedSuccessMessage));
        
        // Verify the correct view name is returned
        assertEquals("redirect:/campaign/my", viewName);
    }

    @Test
    void testViewCampaignDetails_Success() {
        // Given
        String campaignId = testCampaign.getId();
        
        when(campaignService.getCampaignById(campaignId)).thenReturn(testCampaign);
        when(donationService.findDonationsByCampaign(campaignId)).thenReturn(testDonations);

        // When
        String viewName = campaignController.viewCampaignDetails(campaignId, model, userDetails);

        // Then
        verify(campaignService).getCampaignById(campaignId);
        verify(model).addAttribute(eq("campaign"), eq(testCampaign));
        verify(model).addAttribute(eq("donations"), eq(testDonations));
        verify(model).addAttribute(eq("currentUser"), any(UUID.class));
        assertEquals("campaign/view", viewName);
    }
    
    @Test
    void testShowEditForm_NotAuthorized() {
        // Given
        Campaign campaignWithDifferentOwner = Campaign.builder()
                .id(campaignId)
                .fundraiser(otherUser)
                .build();
        
        when(campaignService.getCampaignById(campaignId)).thenReturn(campaignWithDifferentOwner);
        
        // When
        String viewName = campaignController.showEditForm(campaignId, userDetails, model, redirectAttributes);
        
        // Then
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
        assertEquals("redirect:/campaign/view/" + campaignId, viewName);
    }
    
    @Test
    void testUpdateCampaign_Success() {
        // Given
        when(campaignService.getCampaignById(campaignId)).thenReturn(testCampaign);
        when(bindingResult.hasErrors()).thenReturn(false);
        
        // When
        String viewName = campaignController.updateCampaign(campaignId, testCampaignDto, bindingResult, userDetails, redirectAttributes);
        
        // Then
        verify(campaignService).updateCampaign(eq(campaignId), eq(testCampaignDto));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        assertEquals("redirect:/campaign/my", viewName);
    }
    
    @Test
    void testUpdateCampaign_NotAuthorized() {
        // Given
        Campaign campaignWithDifferentOwner = Campaign.builder()
                .id(campaignId)
                .fundraiser(otherUser)
                .build();
                
        when(campaignService.getCampaignById(campaignId)).thenReturn(campaignWithDifferentOwner);
        when(bindingResult.hasErrors()).thenReturn(false);
        
        // When
        String viewName = campaignController.updateCampaign(campaignId, testCampaignDto, bindingResult, userDetails, redirectAttributes);
        
        // Then
        verify(campaignService, never()).updateCampaign(anyString(), any(CampaignDto.class));
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
        assertEquals("redirect:/campaign/view/" + campaignId, viewName);
    }
    
    @Test
    void testDeleteCampaign_Success() {
        // Given
        when(campaignService.getCampaignById(campaignId)).thenReturn(testCampaign);
        
        // When
        String viewName = campaignController.deleteCampaign(campaignId, userDetails, redirectAttributes);
        
        // Then
        verify(campaignService).deleteCampaign(eq(campaignId));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        assertEquals("redirect:/campaign/my", viewName);
    }
    
    @Test
    void testDeleteCampaign_NotAuthorized() {
        // Given
        Campaign campaignWithDifferentOwner = Campaign.builder()
                .id(campaignId)
                .fundraiser(otherUser)
                .build();
                
        when(campaignService.getCampaignById(campaignId)).thenReturn(campaignWithDifferentOwner);
        
        // When
        String viewName = campaignController.deleteCampaign(campaignId, userDetails, redirectAttributes);
        
        // Then
        verify(campaignService, never()).deleteCampaign(anyString());
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
        assertEquals("redirect:/campaign/view/" + campaignId, viewName);
    }
    
    @Test
    void testProcessWithdrawal_Success() {
        // Given
        when(campaignService.processCampaignWithdrawal(eq(campaignId), any(UserEntity.class))).thenReturn(true);
        
        // When
        String viewName = campaignController.processWithdrawal(campaignId, userDetails, redirectAttributes);
        
        // Then
        verify(campaignService).processCampaignWithdrawal(eq(campaignId), any(UserEntity.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        assertEquals("redirect:/wallet/balance", viewName);
    }
    
    @Test
    void testProcessWithdrawal_Failure() {
        // Given
        when(campaignService.processCampaignWithdrawal(eq(campaignId), any(UserEntity.class))).thenReturn(false);
        
        // When
        String viewName = campaignController.processWithdrawal(campaignId, userDetails, redirectAttributes);
        
        // Then
        verify(campaignService).processCampaignWithdrawal(eq(campaignId), any(UserEntity.class));
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
        assertEquals("redirect:/campaign/withdraw/" + campaignId, viewName);
    }
    
    @Test
    void testShowMyCampaigns() {
        // Given
        List<Campaign> campaigns = new ArrayList<>();
        campaigns.add(testCampaign);
        
        when(campaignService.getCampaignsByUser(testUser)).thenReturn(campaigns);
        
        // When
        String viewName = campaignController.showMyCampaigns(userDetails, model);
        
        // Then
        verify(campaignService).updateCampaignStatus(testCampaign.getId());
        verify(campaignService, times(2)).getCampaignsByUser(testUser);
        verify(model).addAttribute(eq("campaignList"), any());
        assertEquals("campaign/my", viewName);
    }
}

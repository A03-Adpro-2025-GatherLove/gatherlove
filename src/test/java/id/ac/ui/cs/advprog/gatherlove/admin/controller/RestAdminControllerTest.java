package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.CampaignResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.TransactionResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.VerifyCampaignRequest;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDashboardService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDonationService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AnnouncementService;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RestAdminControllerTest {

    @Mock
    private AnnouncementService announcementService;

    @Mock
    private AdminDonationService adminDonationService;

    @Mock
    private AdminDashboardService adminDashboardService;

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private RestAdminController restAdminController;

    @Test
    void testAdmin() {
        String result = restAdminController.admin();
        assertEquals("Welcome to the admin dashboard", result);
    }

    @Test
    void testSendAnnouncement() {
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");

        ResponseEntity<String> result = restAdminController.sendAnnouncement(announcement);
        assertEquals("Announcement sent successfully", result.getBody());
        assertEquals(200, result.getStatusCode().value());

        verify(announcementService).sendAnnouncement(announcement);
    }

    @Test
    void testSendAnnouncementInvalid() {
        Announcement announcement = new Announcement();

        ResponseEntity<String> result = restAdminController.sendAnnouncement(announcement);
        assertEquals("Title and content are required", result.getBody());
        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    void testGetTransactions() {
        List<Donation> donations = new ArrayList<>();
        
        Donation donation1 = new Donation();
        donation1.setId(UUID.randomUUID());
        donation1.setCampaignId(UUID.randomUUID());
        donation1.setDonorId(UUID.randomUUID());
        donation1.setAmount(new BigDecimal("100.00"));

        Donation donation2 = new Donation();
        donation2.setId(UUID.randomUUID());
        donation2.setCampaignId(UUID.randomUUID());
        donation2.setDonorId(UUID.randomUUID());
        donation2.setAmount(new BigDecimal("200.00"));

        donations.add(donation1);
        donations.add(donation2);

        when(adminDonationService.getDonationHistory()).thenReturn(donations);

        ResponseEntity<List<TransactionResponse>> result = restAdminController.getTransactions();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        TransactionResponse response1 = result.getBody().get(0);
        assertEquals(donation1.getId(), response1.getId());
        assertEquals(donation1.getCampaignId(), response1.getCampaignId());
        assertEquals(donation1.getDonorId(), response1.getDonorId());

        TransactionResponse response2 = result.getBody().get(1);
        assertEquals(donation2.getId(), response2.getId());
        assertEquals(donation2.getCampaignId(), response2.getCampaignId());
        assertEquals(donation2.getDonorId(), response2.getDonorId());
    }

    @Test
    void testGetStats() {
        Stats stats = new Stats();
        stats.setTotalCampaigns(10L);
        stats.setTotalDonations(20L);
        stats.setTotalUsers(30L);
        stats.setTotalFundRaised(BigDecimal.valueOf(1000.0));

        when(adminDashboardService.getStats()).thenReturn(stats);

        ResponseEntity<Stats> result = restAdminController.getStats();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(stats, result.getBody());
    }

    @Test
    void testGetCampaigns() {
        List<Campaign> campaigns = new ArrayList<>();
        
        Campaign campaign1 = new Campaign();
        campaign1.setId("campaign1");
        campaign1.setTitle("Campaign 1");
        campaigns.add(campaign1);

        Campaign campaign2 = new Campaign();
        campaign2.setId("campaign2");
        campaign2.setTitle("Campaign 2");
        campaigns.add(campaign2);

        when(campaignService.getCampaignsByStatus(null)).thenReturn(campaigns);

        ResponseEntity<List<CampaignResponse>> result = restAdminController.getCampaigns(null);

        assertEquals(org.springframework.http.HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        CampaignResponse response1 = result.getBody().get(0);
        assertEquals(campaign1.getId(), response1.getCampaignId());
        assertEquals(campaign1.getTitle(), response1.getTitle());

        CampaignResponse response2 = result.getBody().get(1);
        assertEquals(campaign2.getId(), response2.getCampaignId());
        assertEquals(campaign2.getTitle(), response2.getTitle());
    }

    @Test
    void testGetCampaignsByStatus() {
        List<Campaign> campaigns = new ArrayList<>();

        Campaign campaign1 = new Campaign();
        campaign1.setId("campaign1");
        campaign1.setTitle("Campaign 1");
        campaign1.setStatus(CampaignStatus.APPROVED);
        campaigns.add(campaign1);

        when(campaignService.getCampaignsByStatus(CampaignStatus.APPROVED)).thenReturn(campaigns);

        ResponseEntity<List<CampaignResponse>> result = restAdminController.getCampaigns(CampaignStatus.APPROVED);

        assertEquals(org.springframework.http.HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());

        CampaignResponse response1 = result.getBody().get(0);
        assertEquals(campaign1.getId(), response1.getCampaignId());
        assertEquals(campaign1.getTitle(), response1.getTitle());
    }

    @Test
    void testVerifyCampaign() {
        Long campaignId = 1L;
        VerifyCampaignRequest request = new VerifyCampaignRequest();
        request.setStatus("APPROVED");

        Campaign campaign = new Campaign();
        campaign.setId(campaignId.toString());

        when(campaignService.getCampaignById(campaignId.toString())).thenReturn(campaign);

        ResponseEntity<?> response = restAdminController.verifyCampaign(campaignId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Campaign verified successfully", response.getBody());
    }

    @Test
    void testVerifyCampaignInvalidStatus() {
        Long campaignId = 1L;
        VerifyCampaignRequest request = new VerifyCampaignRequest();
        request.setStatus("INVALID");

        ResponseEntity<?> response = restAdminController.verifyCampaign(campaignId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Status tidak valid", response.getBody());
    }

    @Test
    void testVerifyCampaignNotFound() {
        Long campaignId = 1L;
        VerifyCampaignRequest request = new VerifyCampaignRequest();
        request.setStatus("APPROVED");

        when(campaignService.getCampaignById(campaignId.toString())).thenReturn(null);

        ResponseEntity<?> response = restAdminController.verifyCampaign(campaignId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteUserSuccess() {
        UUID userId = UUID.randomUUID();
        doNothing().when(adminDashboardService).deleteUser(userId);

        ResponseEntity<?> response = restAdminController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User has been deleted", response.getBody());
    }

    @Test
    void testDeleteUserFailure() {
        UUID userId = UUID.randomUUID();
        doThrow(new RuntimeException()).when(adminDashboardService).deleteUser(userId);

        ResponseEntity<?> response = restAdminController.deleteUser(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error deleting user", response.getBody());
    }
}
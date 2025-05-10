package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.TransactionResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDonationService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AnnouncementService;
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
}
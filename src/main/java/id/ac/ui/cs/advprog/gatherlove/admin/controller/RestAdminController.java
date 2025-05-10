package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.CampaignResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.TransactionResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDashboardService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDonationService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AnnouncementService;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;

@RestController
@RequestMapping("/api/admin")
public class RestAdminController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AdminDonationService adminDonationService;

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private CampaignService campaignService;

    @GetMapping("")
    public String admin() {
        return "Welcome to the admin dashboard";
    }

    @PostMapping("/announcement")
    public ResponseEntity<String> sendAnnouncement(@RequestBody Announcement announcement) {
        if (announcement.getTitle() == null || announcement.getContent() == null) {
            return ResponseEntity.badRequest().body("Title and content are required");
        }
        
        announcementService.sendAnnouncement(announcement);
        return ResponseEntity.ok("Announcement sent successfully");
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions() {
        List<Donation> donations = adminDonationService.getDonationHistory();
        List<TransactionResponse> responses = new ArrayList<>();

        for (Donation donation : donations) {
            TransactionResponse response = new TransactionResponse();
            response.setId(donation.getId());
            response.setCampaignId(donation.getCampaignId());
            response.setDonorId(donation.getDonorId());
            response.setAmount(donation.getAmount());
            response.setMessage(donation.getMessage());
            response.setDonationTimestamp(donation.getDonationTimestamp());
            responses.add(response);
        }

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Stats> getStats() {
        Stats stats = adminDashboardService.getStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignResponse>> getCampaigns(@RequestParam(required = false) CampaignStatus status) {
        List<Campaign> campaigns = campaignService.getCampaignsByStatus(status);
        List<CampaignResponse> responses = campaigns.stream()
                .map(CampaignResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}

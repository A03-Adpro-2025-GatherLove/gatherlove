package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.CampaignResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.TransactionResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.VerifyCampaignRequest;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDashboardService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDonationService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AnnouncementService;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.service.UserService;
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

    @Autowired
    private UserService userService;

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

            Campaign campaign = campaignService.getCampaignById(donation.getCampaignId());
            response.setCampaignTitle(campaign.getTitle());

            UserEntity user = userService.getUserById(donation.getDonorId());
            response.setUsername(user.getUsername());

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

    @PutMapping("/campaigns/{campaign_id}/verify")
    public ResponseEntity<?> verifyCampaign(@PathVariable Long campaign_id, @RequestBody VerifyCampaignRequest request) {
        if (!request.getStatus().equals("APPROVED") && !request.getStatus().equals("REJECTED")) {
            return ResponseEntity.badRequest().body("Status tidak valid");
        }

        Campaign campaign = campaignService.getCampaignById(campaign_id.toString());
        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }

        CampaignStatus status = request.getStatus().equals("APPROVED") ? CampaignStatus.APPROVED : CampaignStatus.REJECTED;
        campaignService.verifyCampaign(campaign.getId(), true);
        return ResponseEntity.ok().body("Campaign verified successfully");
    }

    @DeleteMapping("/api/admin/users/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID user_id) {
        try {
            adminDashboardService.deleteUser(user_id);
            return ResponseEntity.ok().body("User has been deleted");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user");
        }
    }
}

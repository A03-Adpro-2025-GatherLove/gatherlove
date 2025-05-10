package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.TransactionResponse;
import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDashboardService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDonationService;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AnnouncementService;
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
}

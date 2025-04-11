package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.donation.command.*;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
//import id.ac.ui.cs.advprog.gatherlove.auth.models.UserDetailsImpl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // For method security
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;
    @Autowired
    public DonationController(DonationService s) { this.donationService = s; }

    @PostMapping public ResponseEntity<DonationResponse> makeDonation(@RequestBody CreateDonationRequest r) { return null; } // Placeholder
    @DeleteMapping("/{id}/message") public ResponseEntity<DonationResponse> removeMessage(@PathVariable UUID id) { return null; } // Placeholder
    @GetMapping("/my-history") public ResponseEntity<List<DonationResponse>> getMyDonationHistory() { return null; } // Placeholder
    @GetMapping("/campaign/{id}") public ResponseEntity<List<DonationResponse>> getDonationsForCampaign(@PathVariable UUID id) { return null; } // Placeholder
    @GetMapping("/admin/all") public ResponseEntity<List<DonationResponse>> getAllDonations() { return null; } // Placeholder

    // Placeholder helpers needed
    private UUID getCurrentUserId() { return UUID.randomUUID(); }
    private DonationResponse mapToDonationResponse(Donation d) { return null; }
}
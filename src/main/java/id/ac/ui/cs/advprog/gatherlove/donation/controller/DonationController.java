package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.donation.command.*;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import id.ac.ui.cs.advprog.gatherlove.auth.models.UserDetailsImpl; // Assuming UserDetailsImpl has getId()

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

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService; // Use final with constructor injection

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    // Helper method to get current authenticated user's ID
    // IMPORTANT: Adapt this to your actual UserDetails implementation
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            // Assuming UserDetailsImpl has a getId() method returning UUID
            // OR if it stores String: return UUID.fromString(userDetails.getId());
            return userDetails.getId(); // Adapt this line!
        }
        // Handle cases where user is not authenticated or principal is not expected type
        // Throwing an exception might be appropriate if endpoint requires authentication
        System.err.println("Warning: Could not extract UUID from authenticated principal."); // Log error
        throw new IllegalStateException("User authentication details not found or invalid.");
        // Or return null and handle it in the calling methods if anonymous access is sometimes allowed
    }


    // Helper to map Donation entity to DonationResponse DTO
    private DonationResponse mapToDonationResponse(Donation donation) {
        if (donation == null) return null;
        return DonationResponse.builder()
                .id(donation.getId())
                .campaignId(donation.getCampaignId())
                .donorId(donation.getDonorId())
                .amount(donation.getAmount())
                .message(donation.getMessage())
                .donationTimestamp(donation.getDonationTimestamp())
                .build();
    }

    // Endpoint to make a donation (Uses Command Pattern)
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Ensure user is logged in
    public ResponseEntity<DonationResponse> makeDonation(@Valid @RequestBody CreateDonationRequest request) {
        UUID donorId = getCurrentUserId();

        Command<Donation> command = new MakeDonationCommand(
                donationService,
                donorId,
                request.getCampaignId(),
                request.getAmount(),
                request.getMessage()
        );

        Donation createdDonation = command.execute();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDonationResponse(createdDonation));
    }

    // Endpoint to remove a donation message (Uses Command Pattern)
    @DeleteMapping("/{donationId}/message")
    @PreAuthorize("isAuthenticated()") // Ensure user is logged in
    public ResponseEntity<DonationResponse> removeMessage(@PathVariable UUID donationId) {
        UUID userId = getCurrentUserId();

        Command<Donation> command = new RemoveDonationMessageCommand(donationService, donationId, userId);
        Donation updatedDonation = command.execute();
        return ResponseEntity.ok(mapToDonationResponse(updatedDonation));
    }


    // Endpoint to get user's own donation history (Direct Service Call)
    @GetMapping("/my-history")
    @PreAuthorize("isAuthenticated()") // Ensure user is logged in
    public ResponseEntity<List<DonationResponse>> getMyDonationHistory() {
        UUID userId = getCurrentUserId();
        List<Donation> donations = donationService.findDonationsByDonor(userId);
        List<DonationResponse> response = donations.stream()
                .map(this::mapToDonationResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Endpoint to get donations for a specific campaign (Direct Service Call)
    // Usually public, no specific auth needed beyond potential general API access
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<DonationResponse>> getDonationsForCampaign(@PathVariable UUID campaignId) {
        List<Donation> donations = donationService.findDonationsByCampaign(campaignId);
        List<DonationResponse> response = donations.stream()
                .map(this::mapToDonationResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // --- Admin Endpoint Example ---
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')") // Only allow users with ADMIN role
    public ResponseEntity<List<DonationResponse>> getAllDonations() {
        List<Donation> donations = donationService.findAllDonations();
        List<DonationResponse> response = donations.stream()
                .map(this::mapToDonationResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
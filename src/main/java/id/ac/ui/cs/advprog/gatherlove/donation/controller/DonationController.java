package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.donation.command.MakeDonationCommand;
import id.ac.ui.cs.advprog.gatherlove.donation.command.RemoveDonationMessageCommand;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.DonationNotFoundException;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.UnauthorizedException;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
public class DonationController {

    @Autowired
    private DonationService donationService;

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (UserDetailsImpl) auth.getPrincipal();
        return principal.getId();
    }

    @PostMapping("/api/donations")
    public CompletableFuture<ResponseEntity<DonationResponse>> makeDonation(@RequestBody CreateDonationRequest req) {
        UUID userId = UUID.fromString("01e108d3-349b-400b-a63e-b3b834e7fb21");

        return donationService.createDonation(
                        userId, req.getCampaignId(), req.getAmount(), req.getMessage())
                .thenApply(donation -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(DonationResponse.from(donation)))
                .exceptionally(ex -> { // Menangani exception dari CompletableFuture
                    // Log a more detailed error here
                    System.err.println("Error during donation creation: " + ex.getMessage());
                    // Berikan response error yang lebih generik atau spesifik
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.badRequest().body(null); // Atau DTO error
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                });
    }

    @GetMapping("/api/donations/campaign/{campaignId}")
    public ResponseEntity<List<DonationResponse>> getByCampaign(@PathVariable String campaignId) {
        List<Donation> list = donationService.findDonationsByCampaign(campaignId); // Metode ini masih sinkronus
        List<DonationResponse> resp = list.stream()
                .map(DonationResponse::from)
                .collect(Collectors.toList()); // Gunakan Collectors.toList() untuk Java 16+ atau toList() untuk Java < 16

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/api/donations/{id}/message")
    public CompletableFuture<ResponseEntity<DonationResponse>> removeMessage(@PathVariable UUID id) {
        UserEntity principal = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = principal.getId();

        // Panggil service async
        return donationService.removeDonationMessage(id, userId)
                .thenApply(donation -> ResponseEntity
                        .ok(DonationResponse.from(donation)))
                .exceptionally(ex -> {
                    System.err.println("Error removing donation message: " + ex.getMessage());
                    if (ex.getCause() instanceof UnauthorizedException) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                    } else if (ex.getCause() instanceof DonationNotFoundException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                });
    }

    @GetMapping("/api/donations/my-history")
    public ResponseEntity<List<DonationResponse>> getMyHistory() {
        UUID userId = ((UserEntity)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getId();

        List<Donation> list = donationService.findDonationsByDonor(getCurrentUserId());
        List<DonationResponse> resp = list.stream()
                .map(DonationResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resp);
    }
}

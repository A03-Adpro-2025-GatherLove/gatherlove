// File: src/main/java/id/ac/ui/cs/advprog/gatherlove/donation/controller/DonationController.java
package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity; // TODO: Sesuaikan dengan model User (Tadi UserDetailsImpl)
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class DonationController {

    @Autowired
    private DonationService donationService;

    @PostMapping("/api/donations")
    public ResponseEntity<DonationResponse> makeDonation(@RequestBody CreateDonationRequest req) {
        UUID userId = ((UserEntity)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getId();

        Donation d = donationService.createDonation(
                userId, req.getCampaignId(), req.getAmount(), req.getMessage());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DonationResponse.from(d)); // Saya buat method .from(Donation d), tolong sesuaikan dengan rencana anda - Yasin
    }

    @GetMapping("/api/donations/campaign/{campaignId}")
    public ResponseEntity<List<DonationResponse>> getByCampaign(@PathVariable UUID campaignId) {
        List<Donation> list = donationService.findDonationsByCampaign(campaignId);
        List<DonationResponse> resp = list.stream()
                .map(DonationResponse::from)
                .toList();

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/api/donations/{id}/message")
    public ResponseEntity<DonationResponse> removeMessage(@PathVariable UUID id) {
        UUID userId = ((UserEntity)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getId();

        Donation d = donationService.removeDonationMessage(id, userId);
        return ResponseEntity.ok(DonationResponse.from(d));
    }

    @GetMapping("/api/donations/my-history")
    public ResponseEntity<List<DonationResponse>> getMyHistory() {
        UUID userId = ((UserEntity)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getId();

        List<Donation> list = donationService.findDonationsByDonor(userId);
        List<DonationResponse> resp = list.stream()
                .map(DonationResponse::from)
                .toList();

        return ResponseEntity.ok(resp);
    }
}

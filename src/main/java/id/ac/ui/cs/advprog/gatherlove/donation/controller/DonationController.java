// File: src/main/java/id/ac/ui/cs/advprog/gatherlove/donation/controller/DonationController.java
package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.auth.models.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DonationController {

    @Autowired
    private DonationService donationService;

    @PostMapping("/api/donations")
    public ResponseEntity<DonationResponse> makeDonation(@RequestBody CreateDonationRequest req) {
        Long userId = ((UserDetailsImpl)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getId();

        Donation d = donationService.createDonation(
                userId, req.getCampaignId(), req.getAmount(), req.getMessage());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DonationResponse.from(d));
    }

    // existing unsupported endpoints remain until next commit...
    @DeleteMapping("/api/donations/{id}/message")
    public ResponseEntity<DonationResponse> removeMessage(@PathVariable UUID id) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/api/donations/my-history")
    public ResponseEntity<java.util.List<DonationResponse>> getMyHistory() {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/api/donations/campaign/{campaignId}")
    public ResponseEntity<java.util.List<DonationResponse>> getByCampaign(@PathVariable UUID campaignId) {
        throw new UnsupportedOperationException();
    }
}

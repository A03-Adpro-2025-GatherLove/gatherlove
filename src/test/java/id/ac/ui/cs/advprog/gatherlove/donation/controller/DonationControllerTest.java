// File: src/main/java/id/ac/ui/cs/advprog/gatherlove/donation/controller/DonationController.java
package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class DonationController {

    // POST /api/donations
    @PostMapping("/api/donations")
    public ResponseEntity<DonationResponse> makeDonation(@RequestBody CreateDonationRequest req) {
        throw new UnsupportedOperationException();
    }

    // DELETE /api/donations/{id}/message
    @DeleteMapping("/api/donations/{id}/message")
    public ResponseEntity<DonationResponse> removeMessage(@PathVariable UUID id) {
        throw new UnsupportedOperationException();
    }

    // GET /api/donations/my-history
    @GetMapping("/api/donations/my-history")
    public ResponseEntity<List<DonationResponse>> getMyHistory() {
        throw new UnsupportedOperationException();
    }

    // GET /api/donations/campaign/{campaignId}
    @GetMapping("/api/donations/campaign/{campaignId}")
    public ResponseEntity<List<DonationResponse>> getByCampaign(@PathVariable UUID campaignId) {
        throw new UnsupportedOperationException();
    }
}

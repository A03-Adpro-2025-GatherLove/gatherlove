package id.ac.ui.cs.advprog.gatherlove.donation.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;

@Data
@Builder // Allows building the response object easily
public class DonationResponse {
    private UUID id;
    private String campaignId;
    private UUID donorId;
    private BigDecimal amount;
    private String message;
    private LocalDateTime donationTimestamp;
    // Add more fields like donorName, campaignTitle if needed later by mapping
    public static DonationResponse from(Donation d) {
        return new DonationResponse(
                d.getId(),d.getCampaignId(),d.getDonorId(),d.getAmount(),d.getMessage(),d.getDonationTimestamp()
        );
    }
}
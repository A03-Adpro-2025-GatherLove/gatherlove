package id.ac.ui.cs.advprog.gatherlove.donation.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder // Allows building the response object easily
public class DonationResponse {
    private UUID id;
    private UUID campaignId;
    private UUID donorId;
    private BigDecimal amount;
    private String message;
    private LocalDateTime donationTimestamp;
    // Add more fields like donorName, campaignTitle if needed later by mapping
}
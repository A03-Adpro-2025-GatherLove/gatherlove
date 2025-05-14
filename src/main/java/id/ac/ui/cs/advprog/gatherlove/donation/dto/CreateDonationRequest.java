package id.ac.ui.cs.advprog.gatherlove.donation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data // Generates getters, setters, equals, hashCode, toString
public class CreateDonationRequest {

    @NotNull(message = "Campaign ID cannot be null")
    private UUID campaignId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String message; // Optional
}
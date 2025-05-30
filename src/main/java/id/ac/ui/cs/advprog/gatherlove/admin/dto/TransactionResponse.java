package id.ac.ui.cs.advprog.gatherlove.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {
    private UUID id;
    private String campaignTitle;
    private String username;
    private BigDecimal amount;
    private String message;
    private LocalDateTime donationTimestamp;
}

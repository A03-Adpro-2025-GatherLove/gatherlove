package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DonateRequest(
        Long campaign_id,

        @DecimalMin(value = "5000", message = "Minimum donation amount is Rp. 5.000,-")
        BigDecimal amount
) {}
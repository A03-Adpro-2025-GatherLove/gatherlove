package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record WithdrawRequest(
        @DecimalMin(value = "1", message = "amount must be > 0")
        BigDecimal amount,

        Long campaign_id
) {}
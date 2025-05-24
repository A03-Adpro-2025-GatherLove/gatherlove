package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawRequest(
        @DecimalMin(value = "1", message = "Withdraw amount must be bigger than Rp. 0,-")
        BigDecimal amount,

        @NotNull UUID requestId,

        Long campaign_id
) {}
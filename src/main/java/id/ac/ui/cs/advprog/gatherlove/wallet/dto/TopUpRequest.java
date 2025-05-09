package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TopUpRequest(
        @Pattern(regexp = "GOPAY|DANA", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "The payment method must be GOPAY or DANA")
        String method,

        @NotBlank(message = "phone_number is required")
        String phone_number,

        @DecimalMin(value = "10000", message = "Top-up value must be at least Rp. 10.000,-")
        BigDecimal amount
) {}
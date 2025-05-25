package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record TopUpRequest(
        @Pattern(regexp = "GOPAY|DANA|OVO|BNI|PAYPAL|WISE", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "The payment method must be of those that are provided")
        String method,

        @NotNull UUID requestId,

        @NotBlank(message = "phone_number is required")
        String phone_number,

        @DecimalMin(value = "1000", message = "Top-up value must be at least Rp. 1.000,-")
        BigDecimal amount
) {}
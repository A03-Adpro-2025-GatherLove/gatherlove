package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import java.math.BigDecimal;

public record DonateResponse(
        String message,
        BigDecimal remaining_balance
) {}
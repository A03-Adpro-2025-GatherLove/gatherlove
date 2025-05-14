package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import java.math.BigDecimal;

public record TopUpResponse(String message, BigDecimal new_balance) {}
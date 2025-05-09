package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        Long transaction_id,
        TransactionType type,
        BigDecimal amount,
        LocalDateTime timestamp
) {}
package id.ac.ui.cs.advprog.gatherlove.wallet.dto;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionDto {
    private final Long transaction_id;
    private final String type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String displayType;

    public TransactionDto(Long id, TransactionType type, BigDecimal amount, LocalDateTime timestamp) {
        this.transaction_id = id;
        this.type = type.name();
        this.displayType = switch (type) {
            case TOP_UP    -> "Top-Up to Wallet";
            case WITHDRAW  -> "Withdrawal from Campaign";
            case DONATION  -> "Donation to Campaign";
        };
        this.amount = amount;
        this.timestamp = timestamp;
    }
}

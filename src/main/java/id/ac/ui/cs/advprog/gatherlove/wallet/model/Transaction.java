package id.ac.ui.cs.advprog.gatherlove.wallet.model;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Getter @Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;

    private String paymentMethod;
    private LocalDateTime transactionDateTime;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public Transaction() {
    }

    public Transaction(TransactionType type, BigDecimal amount, String paymentMethod) {
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionDateTime = LocalDateTime.now();
    }
}
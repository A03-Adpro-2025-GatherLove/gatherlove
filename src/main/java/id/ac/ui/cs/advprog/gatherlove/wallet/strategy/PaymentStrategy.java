package id.ac.ui.cs.advprog.gatherlove.wallet.strategy;

import java.math.BigDecimal;

public interface PaymentStrategy {
    boolean pay(BigDecimal amount, String phoneNumber);
}
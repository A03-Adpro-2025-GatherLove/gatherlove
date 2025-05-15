package id.ac.ui.cs.advprog.gatherlove.wallet.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("dana")
public class DanaStrategy implements PaymentStrategy {

    @Override
    public boolean pay(BigDecimal amount, String phoneNumber) {
        System.out.println("Processing DANA payment for " + phoneNumber + ", amount: " + amount);
        return true;
    }
}
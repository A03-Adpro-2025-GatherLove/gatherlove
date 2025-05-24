package id.ac.ui.cs.advprog.gatherlove.wallet.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("bni")
public class BNIStrategy implements PaymentStrategy {

    @Override
    public boolean pay(BigDecimal amount, String phoneNumber) {
        System.out.println("Processing BNI payment for " + phoneNumber + ", amount: " + amount);
        return true;
    }
}
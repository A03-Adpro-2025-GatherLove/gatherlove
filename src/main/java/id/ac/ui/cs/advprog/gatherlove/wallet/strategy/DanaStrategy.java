package id.ac.ui.cs.advprog.gatherlove.wallet.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component("dana")
public class DanaStrategy implements PaymentStrategy {

    @Override
    public boolean pay(BigDecimal amount, String phoneNumber) {
        log.debug("DANA payment request | phone={} | amount={}", phoneNumber, amount);
        return true;
    }
}
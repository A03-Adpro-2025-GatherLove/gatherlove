package id.ac.ui.cs.advprog.gatherlove.wallet.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component("ovo")
public class OVOStrategy implements PaymentStrategy {

    @Override
    public boolean pay(BigDecimal amount, String phoneNumber) {
        log.debug("OVO payment request | phone={} | amount={}", phoneNumber, amount);
        return true;
    }
}
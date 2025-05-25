package id.ac.ui.cs.advprog.gatherlove.wallet.observer;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationObserver implements WalletObserver {

    @Override
    public void onBalanceChanged(Wallet wallet, Transaction tx) {
        log.info("Sending notification | userId={} | type={}", wallet.getUserId(), tx.getType());
    }
}

package id.ac.ui.cs.advprog.gatherlove.wallet.observer;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingObserver implements WalletObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingObserver.class);

    @Override
    public void onBalanceChanged(Wallet wallet, Transaction tx) {
        log.info("Wallet balance changed | userId={} | type={} | amount={}",
                wallet.getUserId(), tx.getType(), tx.getAmount());
    }
}
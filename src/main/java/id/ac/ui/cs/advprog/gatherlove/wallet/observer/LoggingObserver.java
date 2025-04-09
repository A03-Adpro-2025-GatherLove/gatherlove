package id.ac.ui.cs.advprog.gatherlove.wallet.observer;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class LoggingObserver implements WalletObserver {

    @Override
    public void onBalanceChanged(Wallet wallet, Transaction transaction) {
        System.out.println("LoggingObserver: Wallet balance changed. "
                + "UserId: " + wallet.getUserId()
                + ", Transaction: " + transaction.getType()
                + ", Amount: " + transaction.getAmount());
    }
}
package id.ac.ui.cs.advprog.gatherlove.wallet.observer;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class NotificationObserver implements WalletObserver {

    @Override
    public void onBalanceChanged(Wallet wallet, Transaction transaction) {
        System.out.println("NotificationObserver: Sending push/email notification "
                + "for userId: " + wallet.getUserId()
                + ", transaction: " + transaction.getType());
    }
}
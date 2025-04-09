package id.ac.ui.cs.advprog.gatherlove.wallet.observer;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WalletEventPublisher {

    private final List<WalletObserver> observers = new ArrayList<>();

    public void addObserver(WalletObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WalletObserver observer) {
        observers.remove(observer);
    }

    public void notifyBalanceChanged(Wallet wallet, Transaction transaction) {
        for (WalletObserver observer : observers) {
            observer.onBalanceChanged(wallet, transaction);
        }
    }
}
package id.ac.ui.cs.advprog.gatherlove.wallet.observer;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

class WalletObserverTest {

    private WalletEventPublisher publisher;
    private WalletObserver observer1;
    private WalletObserver observer2;

    @BeforeEach
    void setup() {
        publisher = new WalletEventPublisher();
        observer1 = Mockito.mock(WalletObserver.class);
        observer2 = Mockito.mock(WalletObserver.class);
        publisher.addObserver(observer1);
        publisher.addObserver(observer2);
    }

    @Test
    void testBalanceChangeNotification() {
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.valueOf(75000));
        Transaction transaction = new Transaction(TransactionType.TOP_UP, BigDecimal.valueOf(10000), "GOPAY");
        publisher.notifyBalanceChanged(wallet, transaction);

        Mockito.verify(observer1).onBalanceChanged(wallet, transaction);
        Mockito.verify(observer2).onBalanceChanged(wallet, transaction);
    }
}
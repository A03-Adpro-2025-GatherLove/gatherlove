package id.ac.ui.cs.advprog.gatherlove.wallet.config;

import id.ac.ui.cs.advprog.gatherlove.wallet.observer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class WalletConfig {

    @Autowired
    private WalletEventPublisher walletEventPublisher;

    @Autowired
    private LoggingObserver loggingObserver;

    @Autowired
    private NotificationObserver notificationObserver;

    @PostConstruct
    public void registerObservers() {
        walletEventPublisher.addObserver(loggingObserver);
        walletEventPublisher.addObserver(notificationObserver);
    }
}
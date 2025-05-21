package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.observer.WalletEventPublisher;
import id.ac.ui.cs.advprog.gatherlove.wallet.repository.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletEventPublisher walletEventPublisher;
    private final Map<String, PaymentStrategy> strategies;

    public WalletServiceImpl(WalletRepository walletRepository,
                             TransactionRepository transactionRepository,
                             WalletEventPublisher walletEventPublisher,
                             Map<String, PaymentStrategy> strategies) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletEventPublisher = walletEventPublisher;
        this.strategies = strategies;
    }

    @Override
    public Wallet getOrCreateWallet(UUID userId) {
        return walletRepository.findByUserId(userId).orElseGet(
                () -> walletRepository.save(new Wallet(userId, BigDecimal.ZERO))
        );
    }

    @Override
    public Wallet topUp(UUID userId, BigDecimal amount, String phoneNumber, String method) {
        Wallet wallet = getOrCreateWallet(userId);

        PaymentStrategy strategy = strategies.get(method.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported Payment Method: " + method);
        }

        if (!strategy.pay(amount, phoneNumber)) {
            throw new RuntimeException("Payment failed. Please try again.");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        Transaction tx = new Transaction(TransactionType.TOP_UP, amount, method);
        wallet.addTransaction(tx);

        walletRepository.save(wallet);
        transactionRepository.save(tx);
        walletEventPublisher.notifyBalanceChanged(wallet, tx);

        return wallet;
    }

    @Override
    public BigDecimal getWalletBalance(UUID userId) {
        return getOrCreateWallet(userId).getBalance();
    }

    @Override
    public Wallet getWalletWithTransactions(UUID userId) {
        return getOrCreateWallet(userId);
    }

    @Override
    public void deleteTopUpTransaction(UUID userId, Long transactionId) {
        Wallet wallet = getOrCreateWallet(userId);
        Transaction tx = transactionRepository.findById(transactionId).orElseThrow(
                () -> new RuntimeException("Transaction not found")
        );

        if (tx.getType() != TransactionType.TOP_UP) {
            throw new RuntimeException("Cannot delete non top-up transactions");
        }

        wallet.setBalance(wallet.getBalance().subtract(tx.getAmount()));
        wallet.getTransactions().remove(tx);

        transactionRepository.delete(tx);
        walletRepository.save(wallet);
        walletEventPublisher.notifyBalanceChanged(wallet, tx);
    }

    @Override
    public Wallet withdrawFunds(UUID userId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        Transaction tr = new Transaction(TransactionType.WITHDRAW, amount, "SYSTEM");
        wallet.addTransaction(tr);

        walletRepository.save(wallet);
        transactionRepository.save(tr);
        walletEventPublisher.notifyBalanceChanged(wallet, tr);
        return wallet;
    }

    @Override
    public Wallet debit(UUID donorId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(donorId);

        if (wallet.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Saldo tidak mencukupi");

        wallet.setBalance(wallet.getBalance().subtract(amount));

        Transaction tx = new Transaction(TransactionType.DONATION, amount, "SYSTEM");
        wallet.addTransaction(tx);

        walletRepository.save(wallet);
        transactionRepository.save(tx);
        walletEventPublisher.notifyBalanceChanged(wallet, tx);

        return wallet;
    }

    @Async("walletExecutor")
    public CompletableFuture<Wallet> topUpAsync(UUID userId, BigDecimal amount,
                                                String phone, String method) {
        Wallet result = this.topUp(userId, amount, phone, method);
        return CompletableFuture.completedFuture(result);
    }

    @Async("walletExecutor")
    public CompletableFuture<BigDecimal> getBalanceAsync(UUID userId) {
        BigDecimal balance = this.getWalletBalance(userId);
        return CompletableFuture.completedFuture(balance);
    }
}
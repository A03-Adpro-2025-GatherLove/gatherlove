package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.observer.WalletEventPublisher;
import id.ac.ui.cs.advprog.gatherlove.wallet.repository.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletEventPublisher walletEventPublisher;

    @Autowired
    private PaymentStrategy danaStrategy;

    @Autowired
    private PaymentStrategy goPayStrategy;

    public Wallet getOrCreateWallet(Long userId) {
        Optional<Wallet> existing = walletRepository.findByUserId(userId);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            Wallet newWallet = new Wallet(userId, BigDecimal.ZERO);
            return walletRepository.save(newWallet);
        }
    }

    public Wallet topUp(Long userId, BigDecimal amount, String phoneNumber, String method) {
        Wallet wallet = getOrCreateWallet(userId);

        PaymentStrategy strategy;
        if ("GOPAY".equalsIgnoreCase(method)) {
            strategy = goPayStrategy;
        } else if ("DANA".equalsIgnoreCase(method)) {
            strategy = danaStrategy;
        } else {
            throw new IllegalArgumentException("Unsupported Payment Method: " + method);
        }

        boolean paymentSuccess = strategy.pay(amount, phoneNumber);
        if (!paymentSuccess) {
            throw new RuntimeException("Payment failed. Please try again.");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        Transaction transaction = new Transaction(TransactionType.TOP_UP, amount, method);
        wallet.addTransaction(transaction);

        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        walletEventPublisher.notifyBalanceChanged(wallet, transaction);

        return wallet;
    }

    public BigDecimal getWalletBalance(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return wallet.getBalance();
    }

    public Wallet getWalletWithTransactions(Long userId) {
        return getOrCreateWallet(userId);
    }

    public void deleteTopUpTransaction(Long userId, Long transactionId) {
        Wallet wallet = getOrCreateWallet(userId);

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getType() != TransactionType.TOP_UP) {
            throw new RuntimeException("Cannot delete non top-up transactions");
        }

        wallet.setBalance(wallet.getBalance().subtract(tx.getAmount()));
        wallet.getTransactions().remove(tx);

        transactionRepository.delete(tx);
        walletRepository.save(wallet);

        walletEventPublisher.notifyBalanceChanged(wallet, tx);
    }

    public Wallet withdrawFunds(Long userId, BigDecimal amount) {
        Wallet wallet = getOrCreateWallet(userId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount, "SYSTEM");
        wallet.addTransaction(transaction);

        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        walletEventPublisher.notifyBalanceChanged(wallet, transaction);

        return wallet;
    }
}
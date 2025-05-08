package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.observer.WalletEventPublisher;
import id.ac.ui.cs.advprog.gatherlove.wallet.repository.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletEventPublisher walletEventPublisher;
    private final PaymentStrategy danaStrategy;
    private final PaymentStrategy goPayStrategy;

    public WalletServiceImpl(WalletRepository walletRepository,
                             TransactionRepository transactionRepository,
                             WalletEventPublisher walletEventPublisher,
                             @Qualifier("danaStrategy") PaymentStrategy danaStrategy,
                             @Qualifier("goPayStrategy") PaymentStrategy goPayStrategy) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletEventPublisher = walletEventPublisher;
        this.danaStrategy = danaStrategy;
        this.goPayStrategy = goPayStrategy;
    }

    @Override
    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId).orElseGet(
                () -> walletRepository.save(new Wallet(userId, BigDecimal.ZERO))
        );
    }

    @Override
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
    public BigDecimal getWalletBalance(Long userId) {
        return getOrCreateWallet(userId).getBalance();
    }

    @Override
    public Wallet getWalletWithTransactions(Long userId) {
        return getOrCreateWallet(userId);
    }

    @Override
    public void deleteTopUpTransaction(Long userId, Long transactionId) {
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
    public Wallet withdrawFunds(Long userId, BigDecimal amount) {
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
    public void debit(UUID donorId, BigDecimal amount) {
        // TODO: Sesuaikan rencana dengan DonationService
    }
}
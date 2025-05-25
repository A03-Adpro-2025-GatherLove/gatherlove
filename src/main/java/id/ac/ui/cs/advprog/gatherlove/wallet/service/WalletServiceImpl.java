package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.observer.WalletEventPublisher;
import id.ac.ui.cs.advprog.gatherlove.wallet.repository.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.strategy.PaymentStrategy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

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
    public Wallet topUp(UUID userId, BigDecimal amount, String phoneNumber, String method, UUID requestId) {
        Wallet wallet = getOrCreateWallet(userId);
        PaymentStrategy strategy = strategies.get(method.toLowerCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported Payment Method: " + method);
        }

        if (!strategy.pay(amount, phoneNumber)) {
            throw new RuntimeException("Payment failed. Please try again.");
        }

        Transaction tx = new Transaction(TransactionType.TOP_UP, amount, method);
        tx.setRequestId(requestId);
        tx.setWallet(wallet);

        try {
            transactionRepository.save(tx);
        } catch (DataIntegrityViolationException e) {
            return walletRepository.findByUserId(userId).orElseThrow(RuntimeException::new);
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.addTransaction(tx);
        walletRepository.save(wallet);
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

        wallet.getTransactions().remove(tx);

        transactionRepository.delete(tx);
        walletRepository.save(wallet);
    }

    @Override
    public Wallet withdrawFunds(UUID userId, BigDecimal amount, UUID requestId, String campaignName) {
        Wallet wallet = getOrCreateWallet(userId);

        Transaction tr = new Transaction(TransactionType.WITHDRAW, amount, campaignName);
        tr.setRequestId(requestId);
        tr.setWallet(wallet);

        try {
            transactionRepository.save(tr);
        } catch (DataIntegrityViolationException e) {
            return walletRepository.findByUserId(userId).orElseThrow(RuntimeException::new);
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.addTransaction(tr);
        walletRepository.save(wallet);
        walletEventPublisher.notifyBalanceChanged(wallet, tr);
        return wallet;
    }

    @Override
    public Wallet debit(UUID donorId, BigDecimal amount, UUID requestId, String campaignName) {
        Wallet wallet = getOrCreateWallet(donorId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Saldo tidak mencukupi");
        }

        Transaction tx = new Transaction(TransactionType.DONATION, amount, campaignName);
        tx.setRequestId(requestId);
        tx.setWallet(wallet);

        try {
            transactionRepository.save(tx);
        } catch (DataIntegrityViolationException e) {
            return walletRepository.findByUserId(donorId).orElseThrow(RuntimeException::new);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.addTransaction(tx);
        walletRepository.save(wallet);
        walletEventPublisher.notifyBalanceChanged(wallet, tx);
        return wallet;
    }

    @Override
    public Transaction getTransactionById(UUID userId, Long transactionId) {
        Wallet wallet = getOrCreateWallet(userId);

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan"));

        if (!tx.getWallet().getUserId().equals(userId)) {
            throw new RuntimeException("Akses ditolak.");
        }
        return tx;
    }

}
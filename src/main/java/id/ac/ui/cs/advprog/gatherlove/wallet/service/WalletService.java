package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface WalletService {

    Wallet getOrCreateWallet(UUID userId);

    Wallet topUp(UUID userId, BigDecimal amount, String phoneNumber, String method);

    BigDecimal getWalletBalance(UUID userId);

    Wallet getWalletWithTransactions(UUID userId);

    void deleteTopUpTransaction(UUID userId, Long transactionId);

    Wallet withdrawFunds(UUID userId, BigDecimal amount);

    Wallet debit(UUID donorId, BigDecimal amount);

    CompletableFuture<Wallet> topUpAsync(UUID userId, BigDecimal amount,
                                         String phone, String method);

    CompletableFuture<BigDecimal> getBalanceAsync(UUID userId);
}

package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    Wallet getOrCreateWallet(UUID userId);

    Wallet topUp(UUID userId, BigDecimal amount, String phoneNumber, String method, UUID requestId);

    BigDecimal getWalletBalance(UUID userId);

    Wallet getWalletWithTransactions(UUID userId);

    void deleteTopUpTransaction(UUID userId, Long transactionId);

    Wallet withdrawFunds(UUID userId, BigDecimal amount, UUID requestId, String campaignName);

    Wallet debit(UUID donorId, BigDecimal amount, UUID requestId, String campaignName);

    Transaction getTransactionById(UUID userId, Long transactionId);
}

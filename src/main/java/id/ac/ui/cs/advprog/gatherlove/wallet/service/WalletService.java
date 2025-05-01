package id.ac.ui.cs.advprog.gatherlove.wallet.service;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import java.math.BigDecimal;

public interface WalletService {

    Wallet getOrCreateWallet(Long userId);

    Wallet topUp(Long userId, BigDecimal amount, String phoneNumber, String method);

    BigDecimal getWalletBalance(Long userId);

    Wallet getWalletWithTransactions(Long userId);

    void deleteTopUpTransaction(Long userId, Long transactionId);

    Wallet withdrawFunds(Long userId, BigDecimal amount);
}

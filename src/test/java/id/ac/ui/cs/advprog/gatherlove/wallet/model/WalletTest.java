package id.ac.ui.cs.advprog.gatherlove.wallet.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class WalletTest {

    @Test
    void testWalletGettersAndSetters() {
        Wallet wallet = new Wallet();
        wallet.setUserId(100L);
        wallet.setBalance(BigDecimal.valueOf(75000));

        Assertions.assertEquals(100L, wallet.getUserId());
        Assertions.assertEquals(BigDecimal.valueOf(75000), wallet.getBalance());
    }

    @Test
    void testWalletAssociationWithTransaction() {
        Wallet wallet = new Wallet(101L, BigDecimal.ZERO);
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(25000));

        wallet.addTransaction(transaction);

        Assertions.assertEquals(1, wallet.getTransactions().size());
        Assertions.assertEquals(wallet, transaction.getWallet());
    }
}
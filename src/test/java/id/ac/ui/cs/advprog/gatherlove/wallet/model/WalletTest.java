package id.ac.ui.cs.advprog.gatherlove.wallet.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class WalletTest {

    @Test
    void testWalletGettersAndSetters() {
        Wallet wallet = new Wallet();
        UUID userID = UUID.randomUUID();
        wallet.setUserId(userID);
        wallet.setBalance(BigDecimal.valueOf(75000));

        Assertions.assertEquals(userID, wallet.getUserId());
        Assertions.assertEquals(BigDecimal.valueOf(75000), wallet.getBalance());
    }

    @Test
    void testWalletAssociationWithTransaction() {
        UUID userID = UUID.randomUUID();
        Wallet wallet = new Wallet(userID, BigDecimal.ZERO);
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(25000));

        wallet.addTransaction(transaction);

        Assertions.assertEquals(1, wallet.getTransactions().size());
        Assertions.assertEquals(wallet, transaction.getWallet());
    }
}
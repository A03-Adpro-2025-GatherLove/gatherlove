package id.ac.ui.cs.advprog.gatherlove.wallet.repository;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.*;
import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    WalletRepository walletRepository;

    @Test
    void testTransactionPersistence() {
        Wallet wallet = new Wallet(103L, BigDecimal.ZERO);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(TransactionType.TOP_UP, BigDecimal.valueOf(6000), "GOPAY");
        transaction.setWallet(wallet);
        transactionRepository.save(transaction);

        Transaction savedTx = transactionRepository.findById(transaction.getId()).orElse(null);
        assertNotNull(savedTx);
        assertEquals(TransactionType.TOP_UP, savedTx.getType());
        assertEquals("GOPAY", savedTx.getPaymentMethod());
    }
}

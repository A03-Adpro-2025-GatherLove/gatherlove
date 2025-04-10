package id.ac.ui.cs.advprog.gatherlove.wallet.model;

import id.ac.ui.cs.advprog.gatherlove.wallet.enums.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class TransactionTest {

    @Test
    void testTransactionDataFields() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(12500));
        transaction.setType(TransactionType.TOP_UP);
        transaction.setPaymentMethod("GOPAY");

        Assertions.assertEquals(TransactionType.TOP_UP, transaction.getType());
        Assertions.assertEquals(BigDecimal.valueOf(12500), transaction.getAmount());
        Assertions.assertEquals("GOPAY", transaction.getPaymentMethod());
    }
}
package id.ac.ui.cs.advprog.gatherlove.wallet.repository;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WalletRepositoryTest {

    @Autowired
    WalletRepository walletRepository;

    @Test
    void testFindByUserIdFound() {
        UUID userID = UUID.randomUUID();
        Wallet wallet = new Wallet(userID, BigDecimal.valueOf(100000));
        walletRepository.save(wallet);

        Optional<Wallet> found = walletRepository.findByUserId(userID);
        assertTrue(found.isPresent(), "Wallet should be found by user's ID");
        assertEquals(BigDecimal.valueOf(100000), found.get().getBalance());
    }

    @Test
    void testFindByUserIdNotFound() {
        Optional<Wallet> found = walletRepository.findByUserId(UUID.randomUUID());
        assertFalse(found.isPresent());
    }
}
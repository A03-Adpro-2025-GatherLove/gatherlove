package id.ac.ui.cs.advprog.gatherlove.wallet.repository;

import id.ac.ui.cs.advprog.gatherlove.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(UUID userId);
}
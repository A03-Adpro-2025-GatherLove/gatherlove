package id.ac.ui.cs.advprog.gatherlove.wallet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByRequestId(UUID requestId);

    Page<Transaction> findByWalletUserId(UUID userId, Pageable pageable);
}
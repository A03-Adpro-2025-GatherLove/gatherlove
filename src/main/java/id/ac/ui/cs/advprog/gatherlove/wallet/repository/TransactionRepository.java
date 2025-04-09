package id.ac.ui.cs.advprog.gatherlove.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import id.ac.ui.cs.advprog.gatherlove.wallet.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
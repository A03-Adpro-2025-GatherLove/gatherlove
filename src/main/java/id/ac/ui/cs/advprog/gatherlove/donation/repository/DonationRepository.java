package id.ac.ui.cs.advprog.gatherlove.donation.repository;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Add repository annotation later
import java.util.UUID;

// Minimal interface extending JpaRepository
public interface DonationRepository extends JpaRepository<Donation, UUID> {
}
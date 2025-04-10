package id.ac.ui.cs.advprog.gatherlove.donation.repository;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository // Added
public interface DonationRepository extends JpaRepository<Donation, UUID> {
    List<Donation> findByDonorIdOrderByDonationTimestampDesc(UUID donorId);
    List<Donation> findByCampaignIdOrderByDonationTimestampDesc(UUID campaignId);
}
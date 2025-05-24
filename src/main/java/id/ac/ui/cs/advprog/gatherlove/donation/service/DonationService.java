package id.ac.ui.cs.advprog.gatherlove.donation.service;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DonationService {
    CompletableFuture<Donation> createDonation(UUID donorId, UUID campaignId, BigDecimal amount, String message);
    CompletableFuture<Donation> removeDonationMessage(UUID donationId, UUID requestingUserId);
    List<Donation> findDonationsByDonor(UUID donorId);
    Donation findDonationById(UUID donationId);
    List<Donation> findDonationsByCampaign(UUID campaignId);
    List<Donation> findAllDonations();
}
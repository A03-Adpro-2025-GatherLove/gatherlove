package id.ac.ui.cs.advprog.gatherlove.donation.service;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final WalletService walletService; // Assume interface exists
    private final CampaignService campaignService; // Assume interface exists

    @Autowired
    public DonationServiceImpl(DonationRepository r, WalletService w, CampaignService c) {
        this.donationRepository = r; this.walletService = w; this.campaignService = c;
    }

    private Donation findDonationByIdInternal(UUID donationId) {
        return donationRepository.findById(donationId)
                .orElseThrow(() -> new DonationNotFoundException("Donation not found with ID: " + donationId));
    }


    @Transactional // Transaksi tetap berlaku untuk operasi dalam method ini
    @Async("asyncTaskExecutor") // Menjalankan method ini di thread pool yang didefinisikan
    public CompletableFuture<Donation> createDonation(UUID donorId, String campaignId, BigDecimal amount, String message) {
        // Simulasi delay untuk menunjukkan sifat async (opsional, hapus untuk produksi)
        // try {
        //     Thread.sleep(5000); // Delay 5 detik
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        // }

        campaignService.validateCampaignForDonation(campaignId);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Donation amount must be positive.");
        }
        walletService.getOrCreateWallet(donorId);
        walletService.debit(donorId, amount);
        Donation donation = Donation.builder()
                .donorId(donorId).campaignId(campaignId).amount(amount).message(message)
                .donationTimestamp(LocalDateTime.now()).build();
        Donation savedDonation = donationRepository.save(donation);
        campaignService.addDonationToCampaign(campaignId, amount);

        return CompletableFuture.completedFuture(savedDonation); // Wrap hasil dalam CompletableFuture
    }

    @Override
    @Transactional
    @Async("asyncTaskExecutor") // Menjalankan method ini di thread pool
    public CompletableFuture<Donation> removeDonationMessage(UUID donationId, UUID requestingUserId)
    {
        Donation donation = findDonationByIdInternal(donationId);
        if (!donation.getDonorId().equals(requestingUserId)) {
            throw new UnauthorizedException("User not authorized to modify this donation message.");
        }
        donation.setMessage(null);
        Donation updatedDonation = donationRepository.save(donation);
        return CompletableFuture.completedFuture(updatedDonation); // Wrap hasil
    }

    @Override
    public Donation findDonationById(UUID donationId) {
        return findDonationByIdInternal(donationId);
    }

    @Override public List<Donation> findDonationsByDonor(UUID donorId) { return donationRepository.findByDonorIdOrderByDonationTimestampDesc(donorId); }
    @Override public List<Donation> findDonationsByCampaign(String campaignId) { return donationRepository.findByCampaignIdOrderByDonationTimestampDesc(campaignId); }
    @Override public List<Donation> findAllDonations() { return donationRepository.findAll(); }
}
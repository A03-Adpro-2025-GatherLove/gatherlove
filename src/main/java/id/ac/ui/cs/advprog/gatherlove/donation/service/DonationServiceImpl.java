package id.ac.ui.cs.advprog.gatherlove.donation.service;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final WalletService walletService; // Assume interface exists
    private final CampaignService campaignService; // Assume interface exists

    @Autowired
    public DonationServiceImpl(DonationRepository r, WalletService w, CampaignService c) {
        this.donationRepository = r; this.walletService = w; this.campaignService = c;
    }

    @Override public Donation createDonation(UUID d, UUID c, BigDecimal a, String m) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public Donation removeDonationMessage(UUID d, UUID u) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public List<Donation> findDonationsByDonor(UUID d) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public Donation findDonationById(UUID d) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public List<Donation> findDonationsByCampaign(UUID c) { throw new UnsupportedOperationException("Not implemented"); }
    @Override public List<Donation> findAllDonations() { throw new UnsupportedOperationException("Not implemented"); }
}
package id.ac.ui.cs.advprog.gatherlove.donation.command;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;

import java.math.BigDecimal;
import java.util.UUID;

public class MakeDonationCommand implements Command<Donation> {

    private final DonationService donationService; // Receiver
    private final UUID donorId;
    private final UUID campaignId;
    private final BigDecimal amount;
    private final String message;

    public MakeDonationCommand(DonationService donationService, UUID donorId, UUID campaignId, BigDecimal amount, String message) {
        this.donationService = donationService;
        this.donorId = donorId;
        this.campaignId = campaignId;
        this.amount = amount;
        this.message = message;
    }

    @Override
    public Donation execute() {
        return donationService.createDonation(donorId, campaignId, amount, message);
    }
}
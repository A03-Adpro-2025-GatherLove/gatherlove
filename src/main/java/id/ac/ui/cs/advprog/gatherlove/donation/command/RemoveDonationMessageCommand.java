package id.ac.ui.cs.advprog.gatherlove.donation.command;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RemoveDonationMessageCommand implements Command<Donation> {

    private final DonationService donationService; // Receiver
    private final UUID donationId;
    private final UUID requestingUserId;

    public RemoveDonationMessageCommand(DonationService donationService, UUID donationId, UUID requestingUserId) {
        this.donationService = donationService;
        this.donationId = donationId;
        this.requestingUserId = requestingUserId;
    }

    @Override
    public CompletableFuture<Donation> execute() { // Ubah return type
        return donationService.removeDonationMessage(donationId, requestingUserId);
    }
}
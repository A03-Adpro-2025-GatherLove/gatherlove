package id.ac.ui.cs.advprog.gatherlove.donation.command;

// Import the specific model needed for the generic type <Donation>
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;

// Import necessary types for method signature
import java.util.UUID;

public class RemoveDonationMessageCommand implements Command<Donation> {

    // Fields for dependencies and parameters will be added later (in GREEN phase)
    // private final DonationService donationService;
    // private final UUID donationId;
    // private final UUID requestingUserId;

    // Constructor will be added later (in GREEN phase)
    // public RemoveDonationMessageCommand(DonationService donationService, UUID donationId, UUID requestingUserId) { ... }

    @Override
    public Donation execute() {
        // The actual implementation calling donationService.removeDonationMessage(...)
        // will be added in the GREEN phase commit.
        throw new UnsupportedOperationException("RemoveDonationMessageCommand execute() is not implemented yet.");
    }
}
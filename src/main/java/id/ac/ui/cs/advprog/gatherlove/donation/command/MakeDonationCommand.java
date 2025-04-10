package id.ac.ui.cs.advprog.gatherlove.donation.command;

// Import the specific model needed for the generic type <Donation>
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;

// Import necessary types for method signature (even if not used in skeleton body)
import java.math.BigDecimal;
import java.util.UUID;

public class MakeDonationCommand implements Command<Donation> {

    // Fields for dependencies and parameters will be added later (in GREEN phase)
    // private final DonationService donationService;
    // private final UUID donorId;
    // private final UUID campaignId;
    // private final BigDecimal amount;
    // private final String message;

    // Constructor will be added later (in GREEN phase)
    // public MakeDonationCommand(DonationService donationService, UUID donorId, ...) { ... }

    @Override
    public Donation execute() {
        // The actual implementation calling donationService.createDonation(...)
        // will be added in the GREEN phase commit.
        throw new UnsupportedOperationException("MakeDonationCommand execute() is not implemented yet.");
    }
}
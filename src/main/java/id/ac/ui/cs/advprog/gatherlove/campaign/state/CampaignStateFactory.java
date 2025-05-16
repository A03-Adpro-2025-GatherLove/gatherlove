package id.ac.ui.cs.advprog.gatherlove.campaign.state;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;

public class CampaignStateFactory {
    public static CampaignState getState(CampaignStatus status) {
        return switch (status) {
            case PENDING_VERIFICATION -> new PendingVerificationState();
            case APPROVED -> new ApprovedState();
            case REJECTED -> new RejectedState();
            case COMPLETED -> new CompletedState();
        };
    }
}
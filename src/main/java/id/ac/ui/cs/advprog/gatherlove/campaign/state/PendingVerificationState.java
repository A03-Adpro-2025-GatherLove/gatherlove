package id.ac.ui.cs.advprog.gatherlove.campaign.state;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;

import java.math.BigDecimal;

public class PendingVerificationState implements CampaignState {
    
    @Override
    public String getStateName() {
        return "PENDING_VERIFICATION";
    }
    
    @Override
    public Campaign verify(Campaign campaign, boolean approve) {
        if (approve) {
            campaign.setStatus(CampaignStatus.APPROVED);
        } else {
            campaign.setStatus(CampaignStatus.REJECTED);
        }
        return campaign;
    }
    
    @Override
    public Campaign donate(Campaign campaign, BigDecimal amount) {
        throw new IllegalStateException("Tidak dapat menerima donasi karena kampanye belum diverifikasi");
    }
    
    @Override
    public boolean canEdit(Campaign campaign) {
        return true; 
    }
    
    @Override
    public boolean canDelete(Campaign campaign) {
        return true; 
    }
    
    @Override
    public Campaign checkStatus(Campaign campaign) {
        // No automatic status changes for pending campaigns
        return campaign;
    }
}
package id.ac.ui.cs.advprog.gatherlove.campaign.state;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ApprovedState implements CampaignState {
    
    @Override
    public String getStateName() {
        return "APPROVED";
    }
    
    @Override
    public Campaign verify(Campaign campaign, boolean approve) {
        throw new IllegalStateException("Kampanye sudah diverifikasi");
    }
    
    @Override
    public Campaign donate(Campaign campaign, BigDecimal amount) {
        // Validate amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Donation amount must be positive");
        }
        
        // Update the total donated amount
        BigDecimal newTotalDonated = campaign.getTotalDonated().add(amount);
        campaign.setTotalDonated(newTotalDonated);
        
        // Check if campaign reached target amount
        if (campaign.getTotalDonated().compareTo(campaign.getTargetAmount()) >= 0) {
            campaign.setStatus(CampaignStatus.COMPLETED);
        }

        return campaign;
    }
    
    @Override
    public boolean canEdit(Campaign campaign) {
        return true;
    }
    
    @Override
    public boolean canDelete(Campaign campaign) {
        // Cannot delete campaigns that already have donations
        return campaign.getTotalDonated().compareTo(BigDecimal.ZERO) == 0;
    }
    
    @Override
    public Campaign checkStatus(Campaign campaign) {
        LocalDate now = LocalDate.now();
        
        // Mark as completed if deadline has passed or target amount reached
        if (now.isAfter(campaign.getDeadline()) || 
            campaign.getTotalDonated().compareTo(campaign.getTargetAmount()) >= 0) {
            campaign.setStatus(CampaignStatus.COMPLETED);
        }
        
        return campaign;
    }
}
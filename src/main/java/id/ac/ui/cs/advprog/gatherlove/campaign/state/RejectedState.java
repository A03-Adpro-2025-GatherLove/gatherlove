package id.ac.ui.cs.advprog.gatherlove.campaign.state;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;

import java.math.BigDecimal;

public class RejectedState implements CampaignState {
    
    @Override
    public String getStateName() {
        return "REJECTED";
    }
    
    @Override
    public Campaign verify(Campaign campaign, boolean approve) {
        throw new IllegalStateException("Kampanye yang ditolak tidak dapat diverifikasi ulang");
    }
    
    @Override
    public Campaign donate(Campaign campaign, BigDecimal amount) {
        throw new IllegalStateException("Tidak dapat menerima donasi karena kampanye ditolak");
    }
    
    @Override
    public boolean canEdit(Campaign campaign) {
        return false;
    }
    
    @Override
    public boolean canDelete(Campaign campaign) {
        return true;
    }
    
    @Override
    public Campaign checkStatus(Campaign campaign) {
        // No automatic status changes for rejected campaigns
        return campaign;
    }
}
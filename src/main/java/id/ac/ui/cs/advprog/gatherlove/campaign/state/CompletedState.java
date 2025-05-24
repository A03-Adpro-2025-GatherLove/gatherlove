package id.ac.ui.cs.advprog.gatherlove.campaign.state;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;

import java.math.BigDecimal;

public class CompletedState implements CampaignState {
    
    @Override
    public String getStateName() {
        return "COMPLETED";
    }
    
    @Override
    public Campaign verify(Campaign campaign, boolean approve) {
        throw new IllegalStateException("Kampanye yang sudah selesai tidak dapat diverifikasi");
    }
    
    @Override
    public Campaign donate(Campaign campaign, BigDecimal amount) {
        throw new IllegalStateException("Tidak dapat menerima donasi karena kampanye sudah selesai");
    }
    
    @Override
    public boolean canEdit(Campaign campaign) {
        return false; 
    }
    
    @Override
    public boolean canDelete(Campaign campaign) {
        return false;
    }
    
    @Override
    public Campaign checkStatus(Campaign campaign) {
        // No automatic status changes for completed campaigns
        return campaign;
    }
}
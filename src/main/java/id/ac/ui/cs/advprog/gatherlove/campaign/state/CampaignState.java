package id.ac.ui.cs.advprog.gatherlove.campaign.state;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;

import java.math.BigDecimal;

public interface CampaignState {
    String getStateName();
    Campaign verify(Campaign campaign, boolean approve);
    Campaign donate(Campaign campaign, BigDecimal amount);
    boolean canEdit(Campaign campaign);
    boolean canDelete(Campaign campaign);
    Campaign checkStatus(Campaign campaign);
}
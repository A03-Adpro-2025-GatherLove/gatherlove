package id.ac.ui.cs.advprog.gatherlove.admin.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Stats {
    private Long totalCampaigns;
    private Long totalDonations;
    private Long totalUsers;
    private BigDecimal totalFundRaised;
}

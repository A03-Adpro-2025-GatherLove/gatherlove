package id.ac.ui.cs.advprog.gatherlove.campaign.service;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.repository.CampaignRepository;
import id.ac.ui.cs.advprog.gatherlove.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.units.qual.A;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final WalletService walletService;

    @Override
    public Campaign createCampaign(CampaignDto dto, UserEntity fundraiser) {
        if (dto.getDeadline() != null && dto.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline harus di masa depan");
        }

        Campaign campaign = Campaign.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .targetAmount(dto.getTargetAmount())
                .deadline(dto.getDeadline())
                .imageUrl(dto.getImageUrl())
                .fundraiser(fundraiser)
                .status(CampaignStatus.PENDING_VERIFICATION)
                .build();

        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaignsByUser(UserEntity user) {
        return campaignRepository.findByFundraiser(user);
    }

    @Override
    public Campaign getCampaignById(String id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign tidak ditemukan dengan ID: " + id));
    }

    @Override
    public Campaign updateCampaign(String id, CampaignDto dto) {
        Campaign campaign = getCampaignById(id);
        
        if (!campaign.canEdit()) {
            throw new IllegalStateException("Kampanye tidak dapat diubah dalam status " + campaign.getStatus());
        }
        
        campaign.setTitle(dto.getTitle());
        campaign.setDescription(dto.getDescription());
        campaign.setTargetAmount(dto.getTargetAmount());
        campaign.setDeadline(dto.getDeadline());
        campaign.setImageUrl(dto.getImageUrl());
        
        return campaignRepository.save(campaign);
    }

    @Async("campaignTaskExecutor")
    @Override
    public void deleteCampaign(String id) {
        log.info("Asynchronously deleting campaign with ID: {}", id);
        // This can be async since deletion can happen in background
        Campaign campaign = getCampaignById(id);
        if (!canDeleteCampaign(id)) {
            throw new RuntimeException("Campaign cannot be deleted");
        }
        campaignRepository.delete(campaign);
    }

    @Async("campaignTaskExecutor")
    @Override
    public Campaign verifyCampaign(String id, boolean approve) {
        Campaign campaign = getCampaignById(id);
        campaign.verify(approve);
        return campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public Campaign addDonationToCampaign(String campaignId, BigDecimal amount) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.addDonation(amount);
        campaign.checkStatus();
        return campaignRepository.save(campaign);
    }


    @Override
    public List<Campaign> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status);
    }
    
    @Override
    public boolean canEditCampaign(String id) {
        Campaign campaign = getCampaignById(id);
        return campaign.canEdit();
    }
    
    @Override
    public boolean canDeleteCampaign(String id) {
        Campaign campaign = getCampaignById(id);
        return campaign.canDelete();
    }
    
    @Override
    public Campaign updateCampaignStatus(String id) {
        Campaign campaign = getCampaignById(id);
        campaign.checkStatus();
        return campaignRepository.save(campaign);
    }

    @Override
    public void validateCampaignForDonation(UUID campaignId) {
        Campaign campaign = getCampaignById(campaignId.toString());
        if (campaign.getStatus() != CampaignStatus.APPROVED) {
            throw new IllegalStateException("Cannot donate to campaign with status: " + campaign.getStatus());
        }
    }

    @Async("campaignTaskExecutor")
    @Override
    public void addCollectedAmount(UUID campaignId, BigDecimal amount) {
        Campaign campaign = getCampaignById(campaignId.toString());
        campaign.addDonation(amount);
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public boolean processCampaignWithdrawal(String campaignId, UserEntity user) {
        Campaign campaign = getCampaignById(campaignId);
        UUID userId = user.getId();
        
        // Check if the user is the fundraiser
        if (campaign.getFundraiser() == null || !campaign.getFundraiser().getId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to withdraw funds from this campaign");
        }
        
        // Check if the campaign is completed
        if (campaign.getStatus() != CampaignStatus.COMPLETED) {
            throw new IllegalStateException("Funds can only be withdrawn from completed campaigns");
        }
        
        // Check if there are funds to withdraw
        if (campaign.getTotalDonated() == null || campaign.getTotalDonated().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("No funds available to withdraw");
        }
        
        BigDecimal amountToWithdraw = campaign.getTotalDonated();
        
        // Generate a request ID for this withdrawal
        UUID requestId = UUID.randomUUID();
        
        // Use WalletService to add funds to the fundraiser's wallet
        walletService.withdrawFunds(userId, amountToWithdraw, requestId, campaign.getTitle());
        
        // Set the campaign's total donated to zero to prevent multiple withdrawals
        campaign.setTotalDonated(BigDecimal.ZERO);
        campaignRepository.save(campaign);
        
        log.info("Withdrawal of {} processed for campaign {} by user {}", 
                amountToWithdraw, campaignId, userId);
        
        return true;
    }
}
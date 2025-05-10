package id.ac.ui.cs.advprog.gatherlove.admin.service;

import id.ac.ui.cs.advprog.gatherlove.campaign.repository.CampaignRepository;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;
import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private DonationRepository donationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        
        stats.setTotalCampaigns(campaignRepository.count());
        
        stats.setTotalDonations(donationRepository.count());
        
        stats.setTotalUsers(userRepository.count());
        
        stats.setTotalFundRaised(donationRepository.getTotalFundRaised());
        
        return stats;
    }

    @Override
    public void deleteUser(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }
}

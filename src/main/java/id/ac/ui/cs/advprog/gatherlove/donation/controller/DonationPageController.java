package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/donate") // Atau bisa juga /campaigns/browse
@RequiredArgsConstructor
public class DonationPageController {

    private final CampaignService campaignService;

    @GetMapping("/browse")
    public String browseCampaignsForDonation(Model model) {
        model.addAttribute("approvedCampaigns", campaignService.getCampaignsByStatus(CampaignStatus.APPROVED));
        model.addAttribute("completedCampaigns", campaignService.getCampaignsByStatus(CampaignStatus.COMPLETED));
        return "donation/browse-campaigns"; // Nama file HTML yang akan kita buat
    }
}
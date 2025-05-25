package id.ac.ui.cs.advprog.gatherlove.campaign.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private static final String CREATE_VIEW = "campaign/create";
    private final DonationService donationService;

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (UserDetailsImpl) auth.getPrincipal();
        return principal.getId();
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("campaignDto")) {
            model.addAttribute("campaignDto", new CampaignDto());
        }
        return CREATE_VIEW;
    }

    @PostMapping("/create")
    public String createCampaign(
            @Valid @ModelAttribute("campaignDto") CampaignDto campaignDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserEntity user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return CREATE_VIEW;
        }

        campaignService.createCampaign(campaignDto, user);
        redirectAttributes.addFlashAttribute("successMessage", "Kampanye berhasil dibuat!");

        return "redirect:/campaign/my";
    }

    @GetMapping("/view/{id}")
    public String viewCampaignDetails(@PathVariable("id") String id, Model model) {
        try {
            Campaign campaign = campaignService.getCampaignById(id);
            
            // Format the deadline date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            String formattedDeadline = campaign.getDeadline().format(formatter);
            
            // Calculate progress percentage
            BigDecimal progress = BigDecimal.ZERO;
            if (campaign.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal donatedAmount = campaign.getTotalDonated();
                progress = donatedAmount
                        .multiply(new BigDecimal("100"))
                        .divide(campaign.getTargetAmount(), 2, RoundingMode.HALF_UP);
            }
            
            // TODO: Fetch fundraiser name from the campaign
            String fundraiserName = campaign.getFundraiser() != null ? campaign.getFundraiser().getUsername() : "Unknown";
            model.addAttribute("fundraiserName", fundraiserName);

            // Add data to model
            model.addAttribute("campaign", campaign);
            model.addAttribute("formattedDeadline", formattedDeadline);
            model.addAttribute("progress", progress);

            List<Donation> donations = donationService.findDonationsByCampaign(id);
            model.addAttribute("donations", donations);
            model.addAttribute("currentUser", getCurrentUserId());
            
            // Check if deadline has passed
            boolean deadlinePassed = campaign.getDeadline().isBefore(LocalDate.now());
            model.addAttribute("deadlinePassed", deadlinePassed);
            
            return "campaign/view";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/my")
    public String showMyCampaigns(@AuthenticationPrincipal UserEntity user, Model model) {
        List<Campaign> campaigns = campaignService.getCampaignsByUser(user);
        
        // Update status of campaigns if needed
        campaigns.forEach(campaign -> campaignService.updateCampaignStatus(campaign.getId()));
        
        // Refresh the list after updating statuses
        model.addAttribute("campaignList", campaignService.getCampaignsByUser(user));
        return "campaign/my";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, 
                              @AuthenticationPrincipal UserEntity user,
                              Model model, 
                              RedirectAttributes redirectAttributes) {
        try {
            Campaign campaign = campaignService.getCampaignById(id);
            
            // Check if the current user is the fundraiser
            if (campaign.getFundraiser() == null || !campaign.getFundraiser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak memiliki izin untuk mengedit kampanye ini");
                return "redirect:/campaign/view/" + id;
            }
            
            if (!campaign.canEdit()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Kampanye tidak dapat diubah dalam status " + campaign.getStatus());
                return "redirect:/campaign/my";
            }
            
            CampaignDto dto = new CampaignDto();
            dto.setTitle(campaign.getTitle());
            dto.setDescription(campaign.getDescription());
            dto.setDeadline(campaign.getDeadline());
            dto.setImageUrl(campaign.getImageUrl());
            dto.setTargetAmount(campaign.getTargetAmount());
            model.addAttribute("campaignDto", dto);
            model.addAttribute("campaignId", id);
            return "campaign/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/campaign/my";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String updateCampaign(@PathVariable("id") String id,
                                @Valid @ModelAttribute("campaignDto") CampaignDto dto,
                                BindingResult result,
                                @AuthenticationPrincipal UserEntity user,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "campaign/edit";
        }
    
        try {
            Campaign campaign = campaignService.getCampaignById(id);
            
            // Check if the current user is the fundraiser
            if (campaign.getFundraiser() == null || !campaign.getFundraiser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak memiliki izin untuk mengedit kampanye ini");
                return "redirect:/campaign/view/" + id;
            }
            
            campaignService.updateCampaign(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Kampanye berhasil diperbarui!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/campaign/my";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteCampaign(@PathVariable("id") String id, 
                                @AuthenticationPrincipal UserEntity user,
                                RedirectAttributes redirectAttributes) {
        try {
            Campaign campaign = campaignService.getCampaignById(id);
            
            // Check if the current user is the fundraiser
            if (campaign.getFundraiser() == null || !campaign.getFundraiser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak memiliki izin untuk menghapus kampanye ini");
                return "redirect:/campaign/view/" + id;
            }
            
            campaignService.deleteCampaign(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kampanye berhasil dihapus!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/campaign/my";
    }
}
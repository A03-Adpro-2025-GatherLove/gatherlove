package id.ac.ui.cs.advprog.gatherlove.campaign.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Campaign campaign = campaignService.getCampaignById(id);
            
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
    public String updateCampaign(@PathVariable String id,
                                 @Valid @ModelAttribute("campaignDto") CampaignDto dto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "campaign/edit";
        }

        try {
            campaignService.updateCampaign(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Kampanye berhasil diperbarui!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/campaign/my";
    }

    @PostMapping("/delete/{id}")
    public String deleteCampaign(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            campaignService.deleteCampaign(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kampanye berhasil dihapus!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/campaign/my";
    }
}
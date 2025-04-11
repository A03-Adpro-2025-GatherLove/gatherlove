package id.ac.ui.cs.advprog.gatherlove.campaign.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.User;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @AuthenticationPrincipal User user,
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
    public String showMyCampaigns(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("campaignList", campaignService.getCampaignsByUser(user));
        return "campaign/my";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Campaign campaign = campaignService.getCampaignById(id);
        CampaignDto dto = new CampaignDto();
        dto.setTitle(campaign.getTitle());
        dto.setDescription(campaign.getDescription());
        dto.setDeadline(campaign.getDeadline());
        dto.setImageUrl(campaign.getImageUrl());
        dto.setTargetAmount(campaign.getTargetAmount());
        model.addAttribute("campaignDto", dto);
        model.addAttribute("campaignId", id);
        return "campaign/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCampaign(@PathVariable String id,
                                 @Valid @ModelAttribute("campaignDto") CampaignDto dto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "campaign/edit";
        }

        campaignService.updateCampaign(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Kampanye berhasil diperbarui!");
        return "redirect:/campaign/my";
    }


}

package id.ac.ui.cs.advprog.gatherlove.campaign.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.User;
import id.ac.ui.cs.advprog.gatherlove.campaign.dto.CampaignDto;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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


}

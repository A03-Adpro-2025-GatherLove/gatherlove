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

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("campaignDto", new CampaignDto());
        return "campaign/create";
    }

    @PostMapping("/create")
    public String createCampaign(
            @Valid @ModelAttribute("campaignDto") CampaignDto campaignDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "campaign/create";
        }

        campaignService.createCampaign(campaignDto, user);
        return "redirect:/campaign/my";
    }
}

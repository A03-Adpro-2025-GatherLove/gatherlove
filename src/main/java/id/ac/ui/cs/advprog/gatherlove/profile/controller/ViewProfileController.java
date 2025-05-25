package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import id.ac.ui.cs.advprog.gatherlove.profile.dto.ProfileResponse;
import id.ac.ui.cs.advprog.gatherlove.profile.service.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ViewProfileController {
    private final ProfileService profileService;

    public ViewProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{profileId}")
    public String viewProfile(@PathVariable UUID profileId, Model model) {
        ProfileResponse profile = profileService.viewProfile(profileId);
        model.addAttribute("profile", profile);
        return "profile/view-profile"; // This returns the view-profile.html template
    }
}

package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.UUID;


@Controller
@RequestMapping("/profile/complete")
public class CompleteProfileController {

    @GetMapping("/{userId}")
    public String getCompleteProfilePage(@PathVariable UUID userId, Model model) {
        // Add any needed data to the model
        model.addAttribute("id", userId);
        return "profile/complete-profile"; // Return the template name for the profile completion page
    }
}


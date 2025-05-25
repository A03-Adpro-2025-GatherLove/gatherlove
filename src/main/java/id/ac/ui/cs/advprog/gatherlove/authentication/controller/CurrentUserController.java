package id.ac.ui.cs.advprog.gatherlove.authentication.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/me")
public class CurrentUserController {

    @GetMapping
    public String getCurrentUserProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return "redirect:/profile/" + userDetails.getId();
    }
}
package id.ac.ui.cs.advprog.gatherlove.authentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        return "home/register";
    }
}

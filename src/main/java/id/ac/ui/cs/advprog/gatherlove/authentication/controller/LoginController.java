package id.ac.ui.cs.advprog.gatherlove.authentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "home/login";
    }
}

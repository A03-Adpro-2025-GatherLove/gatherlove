package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("")
    public String index(Model model) {
        return "admin/index";
    }
}

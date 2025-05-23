package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AdminDashboardService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public String index(Model model) {
        Stats stats = adminDashboardService.getStats();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }
}

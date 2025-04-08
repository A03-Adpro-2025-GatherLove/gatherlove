package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class RestAdminController {

    @GetMapping("")
    public String admin() {
        return "Welcome to the admin dashboard";
    }
}

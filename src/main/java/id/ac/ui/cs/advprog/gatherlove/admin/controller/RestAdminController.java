package id.ac.ui.cs.advprog.gatherlove.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.advprog.gatherlove.admin.service.AnnouncementService;

@RestController
@RequestMapping("/api/admin")
public class RestAdminController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("")
    public String admin() {
        return "Welcome to the admin dashboard";
    }

    @PostMapping("/announcement")
    public ResponseEntity<String> sendAnnouncement(@RequestBody Announcement announcement) {
        if (announcement.getTitle() == null || announcement.getContent() == null) {
            return ResponseEntity.badRequest().body("Title and content are required");
        }
        
        announcementService.sendAnnouncement(announcement);
        return ResponseEntity.ok("Announcement sent successfully");
    }
}

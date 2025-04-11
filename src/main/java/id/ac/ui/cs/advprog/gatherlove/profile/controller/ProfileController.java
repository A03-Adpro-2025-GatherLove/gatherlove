package id.ac.ui.cs.advprog.gatherlove.profile.controller;

import com.yourapp.profile.model.Profile;
import com.yourapp.profile.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @PostMapping("/complete")
    public ResponseEntity<Profile> completeProfile(@RequestBody Profile profile) {
        return ResponseEntity.ok(service.completeProfile(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> viewProfile(@PathVariable Long id) {
        return service.viewProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long id, @RequestBody Profile profile) {
        return ResponseEntity.ok(service.updateProfile(id, profile));
    }

    @DeleteMapping("/{id}/bio")
    public ResponseEntity<Profile> deleteBio(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteBio(id));
    }
}

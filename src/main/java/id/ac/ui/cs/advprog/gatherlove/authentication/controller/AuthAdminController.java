package id.ac.ui.cs.advprog.gatherlove.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.gatherlove.authentication.dto.MessageResponse;
import id.ac.ui.cs.advprog.gatherlove.authentication.dto.SignupRequest;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.service.AuthAdminService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/admin")
public class AuthAdminController {
    
    @Autowired
    private AuthAdminService authAdminService;
    
    /**
     * Endpoint to create an admin user
     * Only accessible by existing admins
     */
    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            // Check if password and confirm password match
            if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Passwords do not match!"));
            }
            
            UserEntity admin = authAdminService.createAdminUser(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword());
            
            return ResponseEntity.ok(new MessageResponse("Admin created successfully with username: " + admin.getUsername()));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}

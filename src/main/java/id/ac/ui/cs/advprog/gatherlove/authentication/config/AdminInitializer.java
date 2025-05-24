package id.ac.ui.cs.advprog.gatherlove.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.gatherlove.authentication.service.AuthAdminService;


@Configuration
public class AdminInitializer {

    @Autowired
    private AuthAdminService authAdminService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.admin.username:admin}")
    private String adminUsername;
    
    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;
    
    @Value("${app.admin.password:admin123}")
    private String adminPassword;
    
    /**
     * Initialize the first admin user when application starts
     * if no user exists in the database
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeFirstAdmin() {
        // Only create default admin if no users exist
        if (userRepository.count() == 0) {
            try {
                authAdminService.createAdminUser(adminUsername, adminEmail, adminPassword);
                System.out.println("Default admin user created: " + adminEmail);
                System.out.println("IMPORTANT: Please change the default admin password after first login!");
            } catch (Exception e) {
                System.err.println("Failed to create default admin: " + e.getMessage());
            }
        }
    }
}

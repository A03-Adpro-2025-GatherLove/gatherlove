package id.ac.ui.cs.advprog.gatherlove.authentication.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.ERole;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.Role;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.RoleRepository;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;
import jakarta.annotation.PostConstruct;

@Service
public class AuthAdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    /**
     * Creates an admin user internally
     */
    public UserEntity createAdminUser(String username, String email, String password) {
        // Check if user already exists
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            throw new RuntimeException("User already exists with this username or email");
        }
        
        // Create new user
        UserEntity user = new UserEntity(username, email, encoder.encode(password));
        
        // Assign admin role
        Set<Role> roles = new HashSet<>();
        
        // Add ADMIN role
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Admin Role not found."));
        roles.add(adminRole);
        
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    /**
     * Initialize roles in database when application starts
     */
    @PostConstruct
    public void initRoles() {
        // Create roles if they don't exist
        if (roleRepository.count() == 0) {
            Role userRole = new Role(ERole.ROLE_USER);
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
        }
    }
}

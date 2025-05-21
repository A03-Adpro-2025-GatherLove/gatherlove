package id.ac.ui.cs.advprog.gatherlove.authentication.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor, getters, and setters
    public UserDetailsImpl(UUID id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
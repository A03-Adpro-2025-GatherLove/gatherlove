package id.ac.ui.cs.advprog.gatherlove.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatherlove.authentication.dto.LoginDto;
import id.ac.ui.cs.advprog.gatherlove.authentication.dto.RegisterDto;
import id.ac.ui.cs.advprog.gatherlove.authentication.enums.RoleEnum;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.Role;
import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.RoleRepository;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    private UserEntity userEntity;
    private Role role;

    @BeforeEach
    public void setup() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");

        role = new Role();
        role.setId(1);
        role.setName(RoleEnum.ROLE_USER);

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        userEntity.setRoles(roles);
    }

    @Test
    public void givenValidCredentials_whenSignin_thenReturnJwt() throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("testToken");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("testToken"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void givenValidSignupData_whenSignup_thenReturnSuccess() throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("newuser");
        registerDto.setEmail("new@example.com");
        registerDto.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(role));
        when(encoder.encode("password")).thenReturn("encodedPassword");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenExistingUsername_whenSignup_thenReturnBadRequest() throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setEmail("new@example.com");
        registerDto.setPassword("password");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenExistingEmail_whenSignup_thenReturnBadRequest() throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("newuser");
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest());
    }
}

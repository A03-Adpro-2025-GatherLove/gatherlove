package id.ac.ui.cs.advprog.gatherlove.authentication.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        // Set secret key and expiration
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyWithAtLeast256BitsForHS256Algorithm");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60000); // 1 minute

        // Create UserDetails
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // Create Authentication
        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    public void whenGenerateJwtToken_thenReturnValidToken() {
        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(token).isNotEmpty();
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("testuser");
    }

    @Test
    public void whenValidateJwtToken_withValidToken_thenReturnTrue() {
        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    public void whenValidateJwtToken_withInvalidToken_thenReturnFalse() {
        // When & Then
        assertThat(jwtUtils.validateJwtToken("invalidToken")).isFalse();
    }

    @Test
    public void whenGetUserNameFromJwtToken_thenReturnUsername() {
        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("testuser");
    }
}
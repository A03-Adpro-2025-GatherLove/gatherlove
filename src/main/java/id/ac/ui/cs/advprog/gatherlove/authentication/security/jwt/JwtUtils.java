package id.ac.ui.cs.advprog.gatherlove.authentication.security.jwt;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.Session;
import id.ac.ui.cs.advprog.gatherlove.authentication.repository.SessionRepository;
import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;
    
    private final SessionRepository sessionRepository;
    
    public JwtUtils(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        
        // Create expiration date
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        // Generate JWT token
        String token = Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
        
        // Save session to database
        Session session = new Session();
        session.setToken(token);
        session.setUser(userPrincipal.getUser());
        session.setExpiryDate(expiryDate.toInstant());
        session.setValid(true);
        
        sessionRepository.save(session);
        
        return token;
    }
    
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            // Check if token is valid by JWT standards
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            
            // Check if token exists in database and is valid
            return sessionRepository.findByToken(authToken)
                    .map(session -> session.isValid() && !session.isExpired())
                    .orElse(false);
            
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
    
    public void invalidateToken(String token) {
        sessionRepository.findByToken(token).ifPresent(session -> {
            session.setValid(false);
            sessionRepository.save(session);
        });
    }
}

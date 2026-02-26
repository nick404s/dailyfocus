package com.nick404s.dailyfocus.service;

import com.nick404s.dailyfocus.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private User user;
    private SecretKey signingKey;

    @BeforeEach
    void setup() {
        jwtService = new JwtServiceImpl();

        // Stable Base64 secret (32 bytes for HS256)
        String secretKey = "dGhpc2lzbXl2ZXJ5c2VjdXJlc2VjcmV0a2V5MTIzNDU2";
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        // Inject fields into JwtServiceImpl
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", secretKey);
        ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION",
                3600000L); // 1 hour

        user = new User();
        user.setEmail("john@example.com");
    }

    // ---------------------------------------------------------
    // generateToken()
    // ---------------------------------------------------------
    @Test
    void generateTokenCreatesValidJwt() {
        String token = jwtService.generateToken(new HashMap<>(), user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // ---------------------------------------------------------
    // extractUsername()
    // ---------------------------------------------------------
    @Test
    void extractUsernameReturnsCorrectEmail() {
        String token = jwtService.generateToken(new HashMap<>(), user);

        String username = jwtService.extractUsername(token);

        assertEquals("john@example.com", username);
    }

    // ---------------------------------------------------------
    // isTokenValid()
    // ---------------------------------------------------------
    @Test
    void isTokenValidReturnsTrueForValidToken() {
        String token = jwtService.generateToken(new HashMap<>(), user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValidReturnsFalseForDifferentUser() {
        String token = jwtService.generateToken(new HashMap<>(), user);

        User other = new User();
        other.setEmail("other@example.com");

        assertFalse(jwtService.isTokenValid(token, other));
    }

    // ---------------------------------------------------------
    // Expiration handling
    // ---------------------------------------------------------
    @Test
    void isTokenValidReturnsFalseForExpiredToken() {
        // Build an expired token manually using the same signing key
        String expiredToken = Jwts.builder()
                .subject("john@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 3600000))
                .expiration(new Date(System.currentTimeMillis() -
                        1000)) // already expired
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        assertFalse(jwtService.isTokenValid(expiredToken, user));
    }

    // ---------------------------------------------------------
    // Custom claims round-trip
    // ---------------------------------------------------------
    @Test
    void extractClaimRetrievesCustomClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = jwtService.generateToken(claims, user);

        // Extract using the same claim resolver pattern as the service
        String role = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

        assertEquals("ADMIN", role);
    }
}

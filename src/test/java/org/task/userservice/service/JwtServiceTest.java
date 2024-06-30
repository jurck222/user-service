package org.task.userservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = "684F5DEC9EC626A4DC3239FA9DBB61B854FEA8DF5B1060DB22EEA836105AEE6F";
    private SecretKey secretKey;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
        secretKey = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET_KEY));
    }

    @Test
    public void testGenerateToken() {
        when(userDetails.getUsername()).thenReturn("testUser@mail.com");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("testUser@mail.com", jwtService.extractUsername(token));
    }

    @Test
    public void testExtractClaim() {
        String token = Jwts.builder()
                .setSubject("testUser@mail.com")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        Function<Claims, String> claimsResolver = Claims::getSubject;

        String subject = jwtService.extractClaim(token, claimsResolver);

        assertEquals("testUser@mail.com", subject);
    }

    @Test
    public void testIsTokenValid() {
        when(userDetails.getUsername()).thenReturn("testUser@mail.com");

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testExtractUsername() {
        String token = Jwts.builder()
                .setSubject("testUser@mail.com")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        String username = jwtService.extractUsername(token);

        assertEquals("testUser@mail.com", username);
    }
}

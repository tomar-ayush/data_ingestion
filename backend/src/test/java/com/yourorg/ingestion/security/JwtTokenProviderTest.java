package com.yourorg.ingestion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "testSecretWhichIsLongEnoughForHmacShaAlgorithm";
    private final long validityInMs = 3600000; // 1 hour
    
    @BeforeEach
    public void setup() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMilliseconds", validityInMs);
        jwtTokenProvider.init(); // Initialize the key
    }
    
    @Test
    public void shouldCreateToken() {
        // Given
        String username = "testuser";
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        
        // When
        String token = jwtTokenProvider.createToken(username, authorities);
        
        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    public void shouldGetAuthenticationFromToken() {
        // Given
        String username = "testuser";
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        String token = jwtTokenProvider.createToken(username, authorities);
        
        // When
        Authentication auth = jwtTokenProvider.getAuthentication(token);
        
        // Then
        assertNotNull(auth);
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        assertEquals(username, auth.getName());
        assertEquals(1, auth.getAuthorities().size());
    }
    
    @Test
    public void shouldGetUsernameFromToken() {
        // Given
        String username = "testuser";
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        String token = jwtTokenProvider.createToken(username, authorities);
        
        // When
        String extractedUsername = jwtTokenProvider.getUsername(token);
        
        // Then
        assertEquals(username, extractedUsername);
    }
    
    @Test
    public void shouldValidateToken() {
        // Given
        String username = "testuser";
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        String token = jwtTokenProvider.createToken(username, authorities);
        
        // When & Then
        assertTrue(jwtTokenProvider.validateToken(token));
    }
    
    @Test
    public void shouldNotValidateExpiredToken() throws Exception {
        // Given
        String username = "testuser";
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        
        // Create an expired token
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "secretKey", testSecret);
        ReflectionTestUtils.setField(expiredProvider, "validityInMilliseconds", -10000); // Negative validity = expired
        expiredProvider.init();
        
        String expiredToken = expiredProvider.createToken(username, authorities);
        
        // When & Then
        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }
}
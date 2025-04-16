package com.yourorg.ingestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.ingestion.model.User;
import com.yourorg.ingestion.repository.UserRepository;
import com.yourorg.ingestion.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    public void registerShouldReturnBadRequestWhenUsernameExists() throws Exception {
        // Given
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword("Password1!");
        user.setEmail("user@example.com");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    public void registerShouldReturnOkWhenUserIsValid() throws Exception {
        // Given
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("Password1!");
        user.setEmail("user@example.com");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void registerShouldFailWithInvalidPasswordFormat() throws Exception {
        // Given
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("weakpass"); // Doesn't meet complexity requirements
        user.setEmail("user@example.com");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Password must be at least 8 characters and contain uppercase, lowercase, number and special character"
                ));
    }

    @Test
    public void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception {
        // Given
        User loginUser = new User();
        loginUser.setUsername("testUser");
        loginUser.setPassword("Password1!");

        // Create mock authentication and user details
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testUser")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.createToken(eq("testUser"), anyCollection()))
                .thenReturn("test.jwt.token");

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test.jwt.token"))
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    public void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        // Given
        User loginUser = new User();
        loginUser.setUsername("testUser");
        loginUser.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username/password"));
    }

    // Helper method for creating a mock Authentication
    private Authentication mock(Class<Authentication> clazz) {
        return org.mockito.Mockito.mock(clazz);
    }
}
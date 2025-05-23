package com.yourorg.ingestion.controller;

import com.yourorg.ingestion.model.User;
import com.yourorg.ingestion.repository.UserRepository;
import com.yourorg.ingestion.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // Manual constructor to replace Lombok's @RequiredArgsConstructor
    public AuthController(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    // Password pattern: At least 8 chars, includes uppercase, lowercase, number and
    // special char
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Validate email format
        if (user.getEmail() == null || !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }

        // Validate password strength
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            return ResponseEntity.badRequest().body(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number and special character");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "User registered successfully");
        responseBody.put("username", user.getUsername());

        return ResponseEntity.ok(responseBody);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser, HttpServletResponse response) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));

            // Generate token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.createToken(
                    userDetails.getUsername(),
                    userDetails.getAuthorities());

            // setting the cookies
            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("token", token);
            cookie.setHttpOnly(true); // Prevent JavaScript access for security
            cookie.setSecure(true); // Use only over HTTPS
            cookie.setPath("/"); // Cookie is available for the entire application
            cookie.setMaxAge(7 * 24 * 60 * 60); // Cookie expires in 7 days
            response.addCookie(cookie);

            // Return response with token
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("username", userDetails.getUsername());
            responseBody.put("token", token);
            return ResponseEntity.ok(responseBody);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username/password");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}

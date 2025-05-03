package com.example.cinema.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinema.dto.UserLoginRequest;
import com.example.cinema.dto.UserRegistrationRequest;
import com.example.cinema.entity.User;
import com.example.cinema.security.JwtUtil;
import com.example.cinema.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token and user ID")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        System.out.println("Login attempt: username=" + request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String username = authentication.getName();
            System.out.println("Authentication successful for: " + username);

            // Tạo token
            String token = jwtUtil.generateToken(username);

            // Lấy userId từ UserService
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
            Long userId = user.getId(); // Giữ kiểu Long

            // Trả về token và userId
            return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", userId
            ));
        } catch (BadCredentialsException e) {
            System.out.println("Bad credentials for: " + request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    public Map<String, String> register(@RequestBody UserRegistrationRequest request) {
        User registeredUser = userService.register(request);
        return Map.of("message", "User registered successfully with username: " + registeredUser.getUsername());
    }

    @GetMapping("/user")
    @Operation(summary = "Get user information", description = "Retrieve authenticated user's information")
    @ApiResponse(responseCode = "200", description = "User information retrieved")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Fetching user info for: " + username);
        return userService.findByUsername(username)
                .map(user -> {
                    System.out.println("User found: " + user.getUsername());
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    System.out.println("User not found: " + username);
                    return ResponseEntity.notFound().build();
                });
    }
}
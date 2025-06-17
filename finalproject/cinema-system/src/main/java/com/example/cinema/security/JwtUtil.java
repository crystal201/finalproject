package com.example.cinema.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private final Set<String> blacklistedTokens = new HashSet<>();

    @PostConstruct
    public void init() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT secret must be configured in application.properties");
        }
        if (expiration <= 0) {
            throw new IllegalStateException("JWT expiration must be positive");
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        System.out.println("Generating token for: " + username);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
        System.out.println("Generated token: " + token);
        return token;
    }

    public String extractUsername(String token) {
        System.out.println("Extracting username from token: " + token);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            System.out.println("Extracted username: " + username);
            return username;
        } catch (JwtException e) {
            System.err.println("Failed to extract username: " + e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        System.out.println("Validating token: " + token);
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            if (isTokenBlacklisted(token)) {
                System.out.println("Token is blacklisted");
                return false;
            }
            System.out.println("Token valid");
            return true;
        } catch (JwtException e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public void blacklistToken(String token) {
        System.out.println("Blacklisting token: " + token);
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
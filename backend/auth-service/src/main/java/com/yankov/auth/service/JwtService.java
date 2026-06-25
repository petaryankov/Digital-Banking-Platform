package com.yankov.auth.service;

import io.jsonwebtoken.Claims;

import java.time.Instant;

public interface JwtService {
    String generateAccessToken(String email, String role);

    String generateRefreshToken(String email, String role);

    String extractTokenType(Claims claims);

    String extractUsernameFromRefreshToken(String token);

    Instant calculateRefreshTokenExpiry();

    Claims validateAndParse(String token, String expectedType);
}



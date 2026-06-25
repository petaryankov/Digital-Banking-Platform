package com.yankov.auth.service.impl;

import com.yankov.auth.exception.InvalidJwtTokenException;
import com.yankov.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static com.yankov.auth.constants.ExceptionMessages.*;
import static com.yankov.auth.constants.JwtConstants.*;

@Service
public class JwtServiceImpl implements JwtService {

    private final Key signingKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtServiceImpl(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {

        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(base64Secret)
        );
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Generate access token
    public String generateAccessToken(String email, String role) {

        return buildToken(email, role, ACCESS_TOKEN_TYPE, accessTokenExpiration);
    }

    // Generate refresh token
    public String generateRefreshToken(String email, String role) {
        return buildToken(email, role, REFRESH_TOKEN_TYPE, refreshTokenExpiration);
    }

    // Extract token type
    public String extractTokenType(Claims claims) {
        return claims.get(TOKEN_TYPE_CLAIM, String.class);
    }

    // Extract username(email)
    public String extractUsernameFromRefreshToken(String token) {
        return validateAndParse(token, REFRESH_TOKEN_TYPE).getSubject();
    }

    // Calculate refresh token expiration instant
    public Instant calculateRefreshTokenExpiry() {
        return Instant.now().plusMillis(refreshTokenExpiration);
    }

    // Validate token and parse
    public Claims validateAndParse(String token, String expectedType) {

        try {
            // Parse JWT and verify signature using configured signing key
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Validate token issuer
            if (!JWT_ISSUER.equals(claims.getIssuer())) {
                throw new InvalidJwtTokenException(INVALID_ISSUER);
            }

            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);

            // Validate token type
            if (!expectedType.equals(tokenType)) {
                throw new InvalidJwtTokenException(INVALID_TOKEN);
            }

            // Validate token expiration
            if (claims.getExpiration().before(new Date())) {
                throw new InvalidJwtTokenException(TOKEN_EXPIRED);
            }

            return claims;

            // Signature, malformed, unsupported, or illegal JWT
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidJwtTokenException(MALFORMED_TOKEN);
        }
    }

    // Internal token builder
    private String buildToken(String email,
                              String role,
                              String tokenType,
                              long expiration) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(email)
                .setIssuer(JWT_ISSUER)
                .claim(TOKEN_ROLE, role)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}

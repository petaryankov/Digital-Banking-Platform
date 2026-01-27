package com.yankov.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import static com.yankov.backend.constants.SecurityConstants.JWT_ISSUER;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}")String base64Secret,
            @Value("${jwt.expiration}") long expiration) {

        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(base64Secret)
        );
        this.expiration = expiration;
    }

    // Generate JWT token using user email
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        System.currentTimeMillis() + expiration))
                .signWith(signingKey,  SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username(email)
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // Validate token
    public boolean isTokenValid(String token) {

        Claims claims = parseClaims(token);

        return JWT_ISSUER.equals(claims.getIssuer())
                && !claims.getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

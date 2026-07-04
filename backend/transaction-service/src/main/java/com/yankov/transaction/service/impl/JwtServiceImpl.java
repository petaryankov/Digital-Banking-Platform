package com.yankov.transaction.service.impl;

import com.yankov.transaction.exception.InvalidJwtTokenException;
import com.yankov.transaction.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

import static com.yankov.transaction.constants.ExceptionMessages.*;
import static com.yankov.transaction.constants.JwtConstants.JWT_ISSUER;
import static com.yankov.transaction.constants.JwtConstants.TOKEN_TYPE_CLAIM;

@Service
public class JwtServiceImpl implements JwtService {

    private final Key signingKey;

    public JwtServiceImpl(
            @Value("${jwt.secret}") String base64Secret) {
        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(base64Secret)
        );
    }

    // Extract token type
    public String extractTokenType(Claims claims) {
        return claims.get(TOKEN_TYPE_CLAIM, String.class);
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

}

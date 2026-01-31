package com.yankov.backend.security;

import com.yankov.backend.exception.InvalidRefreshTokenException;
import com.yankov.backend.exception.RefreshTokenNotFoundException;
import com.yankov.backend.model.RefreshToken;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    // persist refresh token in DB
    public RefreshToken create(User user, String token) {

        Instant tokenExpiry = jwtService.calculateRefreshTokenExpiry();

        return refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(token)
                        .expiresAt(tokenExpiry)
                        .revoked(false)
                        .build()
        );
    }

    //Validate refresh token state
    public RefreshToken validate(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(RefreshTokenNotFoundException::new);

        if (refreshToken.isRevoked()
                || refreshToken.getExpiresAt().isBefore(Instant.now())) {

            throw new InvalidRefreshTokenException();
        }

        return refreshToken;
    }

    // Revoke refresh token
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}

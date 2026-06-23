package com.yankov.backend.service.impl;

import com.yankov.backend.exception.InvalidRefreshTokenException;
import com.yankov.backend.exception.RefreshTokenNotFoundException;
import com.yankov.backend.model.RefreshToken;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.RefreshTokenRepository;
import com.yankov.backend.service.JwtService;
import com.yankov.backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

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

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    // delete refresh tokens
    public void deleteByUserId(long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    // Revoke refresh token
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}

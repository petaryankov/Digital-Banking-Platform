package com.yankov.backend.service.impl;

import com.yankov.backend.enums.Role;
import com.yankov.backend.exception.UserAlreadyExistsException;
import com.yankov.backend.model.RefreshToken;
import com.yankov.backend.model.User;
import com.yankov.backend.model.dto.request.AuthRequestDto;
import com.yankov.backend.model.dto.request.RegisterRequestDto;
import com.yankov.backend.model.dto.response.AuthResponseDto;
import com.yankov.backend.security.JwtService;
import com.yankov.backend.security.RefreshTokenService;
import com.yankov.backend.service.AuthService;
import com.yankov.backend.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static com.yankov.backend.constants.JwtConstants.REFRESH_TOKEN_TYPE;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDto login(AuthRequestDto request) {

        // authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userService.getUserByEmail(request.getEmail());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponseDto(accessToken, refreshToken);
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {

        // prevent duplicate email
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // create USER
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.USER)
                .active(true)
                .build();

        User saved = userService.createUser(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(saved.getEmail());
        String refreshToken = jwtService.generateRefreshToken(saved.getEmail());

        // Persist refresh token
        refreshTokenService.create(saved, refreshToken);

        return new AuthResponseDto(accessToken, refreshToken);
    }

    @Override
    public AuthResponseDto refresh(String refreshToken) {

        RefreshToken stored = refreshTokenService.validate(refreshToken);

        Claims claims = jwtService.validateAndParse(
                stored.getToken(),
                REFRESH_TOKEN_TYPE
        );

        String newAccessToken = jwtService.generateAccessToken(claims.getSubject());

        return new AuthResponseDto(newAccessToken, refreshToken);
    }
}

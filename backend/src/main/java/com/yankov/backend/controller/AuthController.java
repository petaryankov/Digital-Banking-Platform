package com.yankov.backend.controller;

import com.yankov.backend.model.RefreshToken;
import com.yankov.backend.model.User;
import com.yankov.backend.model.dto.request.AuthRequestDto;
import com.yankov.backend.model.dto.request.RefreshTokenRequestDto;
import com.yankov.backend.model.dto.response.AuthResponseDto;
import com.yankov.backend.security.JwtService;
import com.yankov.backend.security.RefreshTokenService;
import com.yankov.backend.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.yankov.backend.constants.JwtConstants.REFRESH_TOKEN_TYPE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    // Authenticate user and return JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {

        // Authenticate user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        User user = userService.getUserByEmail(request.getEmail());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Persist refresh token
        refreshTokenService.create(user, refreshToken);

        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenRequestDto request) {

        RefreshToken stored = refreshTokenService
                .validate(request.getRefreshToken());

        Claims claims = jwtService.validateAndParse(
                stored.getToken(),
                REFRESH_TOKEN_TYPE
        );

        String newAccessToken = jwtService
                .generateAccessToken(claims.getSubject());

        return ResponseEntity.ok(new AuthResponseDto(
                newAccessToken,
                request.getRefreshToken()));
    }
}

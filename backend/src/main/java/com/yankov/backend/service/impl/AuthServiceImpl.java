package com.yankov.backend.service.impl;

import com.yankov.backend.enums.Role;
import com.yankov.backend.exception.UserAlreadyExistsException;
import com.yankov.backend.model.RefreshToken;
import com.yankov.backend.model.User;
import com.yankov.backend.model.dto.request.AuthRequestDto;
import com.yankov.backend.model.dto.request.RegisterRequestDto;
import com.yankov.backend.model.dto.response.AuthResponseDto;
import com.yankov.backend.service.AuthService;
import com.yankov.backend.service.JwtService;
import com.yankov.backend.service.RefreshTokenService;
import com.yankov.backend.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import static com.yankov.backend.constants.ExceptionMessages.INVALID_CREDENTIALS;
import static com.yankov.backend.constants.ExceptionMessages.USER_IS_DEACTIVATED;
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

        try {

            // authenticate credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

        } catch (DisabledException ex) {
            throw new RuntimeException(USER_IS_DEACTIVATED);
        } catch (BadCredentialsException ex) {
            throw new RuntimeException(INVALID_CREDENTIALS);
        }

        User user = userService.getUserByEmail(request.getEmail());


        // generate tokens
        String accessToken = jwtService.generateAccessToken(
                user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(
                user.getEmail(), user.getRole().name());

        // persist refresh token
        refreshTokenService.create(user, refreshToken);

        return new AuthResponseDto(accessToken, refreshToken, user.getRole());
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {

        // prevent duplicate email
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // create user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.USER)
                .active(true)
                .build();

        User savedUser = userService.createUser(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(savedUser.getEmail(), savedUser.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail(), savedUser.getRole().name());

        // persist refresh token
        refreshTokenService.create(savedUser, refreshToken);

        return new AuthResponseDto(accessToken, refreshToken, user.getRole());
    }

    @Override
    public AuthResponseDto refresh(String refreshToken) {

        RefreshToken stored = refreshTokenService.validate(refreshToken);

        Claims claims = jwtService.validateAndParse(
                stored.getToken(),
                REFRESH_TOKEN_TYPE
        );

        // extract user email
        String email = claims.getSubject();

        // find user role
        Role userRole = userService.getUserByEmail(email).getRole();

        // generate new access token
        String newAccessToken = jwtService.generateAccessToken(email, userRole.name());

        return new AuthResponseDto(newAccessToken, refreshToken, userRole);
    }
}

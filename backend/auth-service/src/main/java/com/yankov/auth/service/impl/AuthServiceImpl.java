package com.yankov.auth.service.impl;

import com.yankov.auth.enums.Role;
import com.yankov.auth.exception.UserAlreadyExistsException;
import com.yankov.auth.model.RefreshToken;
import com.yankov.auth.model.User;
import com.yankov.auth.model.dto.request.AuthRequestDto;
import com.yankov.auth.model.dto.request.RegisterRequestDto;
import com.yankov.auth.model.dto.response.AuthResponseDto;
import com.yankov.auth.service.AuthService;
import com.yankov.auth.service.JwtService;
import com.yankov.auth.service.RefreshTokenService;
import com.yankov.auth.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yankov.auth.constants.ExceptionMessages.INVALID_CREDENTIALS;
import static com.yankov.auth.constants.ExceptionMessages.USER_IS_DEACTIVATED;
import static com.yankov.auth.constants.JwtConstants.REFRESH_TOKEN_TYPE;

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
    @Transactional
    public void logout(String refreshToken) {
        // delete the token in db
        refreshTokenService.deleteByRefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {

        // prevent duplicate email
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // create user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
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

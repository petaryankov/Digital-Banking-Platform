package com.yankov.auth.controller;

import com.yankov.auth.model.User;
import com.yankov.auth.model.dto.request.UserCreateRequestDto;
import com.yankov.auth.model.dto.response.UserResponseDto;
import com.yankov.auth.service.RefreshTokenService;
import com.yankov.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yankov.auth.constants.SecurityConstants.USER_OR_ADMIN;
import static com.yankov.auth.constants.SecurityConstants.USER_PROFILE_DATA;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id) {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(toResponse(user));

    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDto> getUserByEmail(
            @RequestParam String email) {

        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(toResponse(user));

    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {

        List<UserResponseDto> users = userService
                .getAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }


    // Create new user
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserCreateRequestDto request) {

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        User savedUser = userService.createUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedUser));
    }

    // admin role activate user
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {

        userService.activateUser(id);

        return ResponseEntity.ok().build();
    }

    // admin role deactivate user
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> adminDeactivateUser(@PathVariable Long id) {

        User user = userService.getUserById(id);

        //remove refresh tokens
        refreshTokenService.deleteByUserId(user.getId());

        //deactivate user
        userService.deactivateUser(user.getEmail());

        return ResponseEntity.ok().build();
    }

    // deactivate itself currently authenticated user
    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateUser(Authentication authentication) {

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        // remove refresh tokens
        refreshTokenService.deleteByUserId(user.getId());

        // deactivate account
        userService.deactivateUser(email);

        return ResponseEntity.noContent().build();

    }

    @PreAuthorize(USER_OR_ADMIN)
    @GetMapping("/profile")
    public String userProfile() {
        return USER_PROFILE_DATA;
    }

    // private mapper
    private UserResponseDto toResponse(User user) {

        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

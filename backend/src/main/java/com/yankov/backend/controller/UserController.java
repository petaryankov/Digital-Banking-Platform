package com.yankov.backend.controller;

import com.yankov.backend.model.User;
import com.yankov.backend.model.dto.request.UserCreateRequestDto;
import com.yankov.backend.model.dto.response.UserResponseDto;
import com.yankov.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.yankov.backend.constants.SecurityConstants.USER_OR_ADMIN;
import static com.yankov.backend.constants.SecurityConstants.USER_PROFILE_DATA;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
                .createdAt(user.getCreatedAt())
                .build();
    }
}

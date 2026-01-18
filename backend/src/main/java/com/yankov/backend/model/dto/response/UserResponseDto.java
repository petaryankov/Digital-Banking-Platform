package com.yankov.backend.model.dto.response;

import com.yankov.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String fullName;

    private String email;

    private Role role;

    private LocalDateTime createdAt;
}

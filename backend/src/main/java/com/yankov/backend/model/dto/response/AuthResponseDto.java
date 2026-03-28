package com.yankov.backend.model.dto.response;

import com.yankov.backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    String accessToken;
    String refreshToken;
    Role role;
}

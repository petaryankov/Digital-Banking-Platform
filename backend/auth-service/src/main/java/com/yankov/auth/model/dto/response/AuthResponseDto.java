package com.yankov.auth.model.dto.response;

import com.yankov.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    String accessToken;
    String refreshToken;
    Role role;
}

package com.yankov.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    String accessToken;
    String refreshToken;
}

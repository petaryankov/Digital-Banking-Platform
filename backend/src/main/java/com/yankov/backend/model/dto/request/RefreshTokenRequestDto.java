package com.yankov.backend.model.dto.request;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RefreshTokenRequestDto {

    private String refreshToken;
}

package com.yankov.auth.service;

import com.yankov.auth.model.dto.request.AuthRequestDto;
import com.yankov.auth.model.dto.request.RegisterRequestDto;
import com.yankov.auth.model.dto.response.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

    void logout(String refreshToken);

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto refresh(String refreshToken);
}


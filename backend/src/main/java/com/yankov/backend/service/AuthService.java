package com.yankov.backend.service;

import com.yankov.backend.model.dto.request.AuthRequestDto;
import com.yankov.backend.model.dto.request.RegisterRequestDto;
import com.yankov.backend.model.dto.response.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

    void logout(String refreshToken);

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto refresh(String refreshToken);
}


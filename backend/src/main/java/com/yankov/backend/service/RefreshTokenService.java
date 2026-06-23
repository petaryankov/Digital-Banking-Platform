package com.yankov.backend.service;

import com.yankov.backend.model.RefreshToken;
import com.yankov.backend.model.User;

public interface RefreshTokenService {

    RefreshToken create(User user, String token);

    RefreshToken validate(String token);

    void deleteByRefreshToken(String refreshToken);

    void deleteByUserId(long userId);

    void revoke(RefreshToken token);
}

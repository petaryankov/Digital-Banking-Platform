package com.yankov.auth.service;

import com.yankov.auth.model.RefreshToken;
import com.yankov.auth.model.User;

public interface RefreshTokenService {

    RefreshToken create(User user, String token);

    RefreshToken validate(String token);

    void deleteByRefreshToken(String refreshToken);

    void deleteByUserId(long userId);

    void revoke(RefreshToken token);
}

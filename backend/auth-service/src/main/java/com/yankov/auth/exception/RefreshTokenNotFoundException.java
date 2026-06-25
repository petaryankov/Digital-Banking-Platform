package com.yankov.auth.exception;

import com.yankov.auth.constants.ExceptionMessages;

public class RefreshTokenNotFoundException extends RuntimeException {

    public RefreshTokenNotFoundException() {
        super(ExceptionMessages.REFRESH_TOKEN_NOT_FOUND);
    }

}

package com.yankov.backend.exception;

import com.yankov.backend.constants.ExceptionMessages;

public class RefreshTokenNotFoundException extends RuntimeException {

    public RefreshTokenNotFoundException() {
        super(ExceptionMessages.REFRESH_TOKEN_NOT_FOUND);
    }

}

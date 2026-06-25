package com.yankov.auth.exception;

import com.yankov.auth.constants.ExceptionMessages;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super(ExceptionMessages.REFRESH_TOKEN_INVALID);
    }
}

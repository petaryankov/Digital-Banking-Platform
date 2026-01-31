package com.yankov.backend.exception;

import com.yankov.backend.constants.ExceptionMessages;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super(ExceptionMessages.REFRESH_TOKEN_INVALID);
    }
}

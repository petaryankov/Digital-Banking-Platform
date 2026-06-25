package com.yankov.auth.exception;

import static com.yankov.auth.constants.ExceptionMessages.USER_NOT_FOUND_BY_EMAIL;
import static com.yankov.auth.constants.ExceptionMessages.USER_NOT_FOUND_BY_ID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(String.format(USER_NOT_FOUND_BY_ID, id));
    }

    public UserNotFoundException(String email) {
        super(String.format(USER_NOT_FOUND_BY_EMAIL, email));
    }

}

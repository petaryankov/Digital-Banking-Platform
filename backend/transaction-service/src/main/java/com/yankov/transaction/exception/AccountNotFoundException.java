package com.yankov.transaction.exception;

import com.yankov.transaction.constants.ExceptionMessages;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long id) {
        super(String.format(
                ExceptionMessages.ACCOUNT_NOT_FOUND_BY_ID, id
        ));
    }

    public AccountNotFoundException(String accountNumber) {
        super(String.format(
                ExceptionMessages.ACCOUNT_NOT_FOUND_BY_ACCOUNT_NUMBER, accountNumber
        ));
    }
}

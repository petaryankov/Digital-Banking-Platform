package com.yankov.transaction.exception;

import com.yankov.transaction.constants.ExceptionMessages;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Long accountId, BigDecimal amount) {
        super(String.format(
                ExceptionMessages.INSUFFICIENT_BALANCE, accountId, amount
        ));
    }
}

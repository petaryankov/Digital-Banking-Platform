package com.yankov.transaction.exception;

import com.yankov.transaction.constants.ExceptionMessages;

import java.math.BigDecimal;

public class InvalidTransactionException extends RuntimeException{

    public InvalidTransactionException(BigDecimal amount) {
        super(String.format(ExceptionMessages.INVALID_TRANSACTION_AMOUNT, amount));
    }
}

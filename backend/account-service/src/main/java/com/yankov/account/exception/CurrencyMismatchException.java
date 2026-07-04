package com.yankov.account.exception;

import static com.yankov.account.constants.ExceptionMessages.CURRENCY_MISMATCH;

public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException(String sourceCurrency, String targetCurrency) {
        super(String.format(CURRENCY_MISMATCH, sourceCurrency, targetCurrency));
    }
}

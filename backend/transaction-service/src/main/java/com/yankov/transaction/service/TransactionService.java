package com.yankov.transaction.service;

import com.yankov.transaction.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);

    Transaction deposit(String targetAccountNumber, BigDecimal amount);

    Transaction withdraw(String sourceAccountNumber, BigDecimal amount);

    Transaction transfer(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount);

    List<Transaction> getTransactionsBySourceAccountNumber(String sourceAccountNumber);

    List<Transaction> getTransactionsByTargetAccountNumber(String targetAccountNumber);
}

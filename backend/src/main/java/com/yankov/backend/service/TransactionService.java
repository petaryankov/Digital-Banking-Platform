package com.yankov.backend.service;

import com.yankov.backend.model.Account;
import com.yankov.backend.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);

    Transaction deposit(Account target, BigDecimal amount);

    Transaction withdraw(Account source, BigDecimal amount);

    Transaction transfer(Account source, Account target, BigDecimal amount);

    List<Transaction> getTransactionsBySourceAccount(Account account);

    List<Transaction> getTransactionsByTargetAccount(Account account);
}

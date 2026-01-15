package com.yankov.backend.service.impl;

import com.yankov.backend.enums.TransactionStatus;
import com.yankov.backend.enums.TransactionType;
import com.yankov.backend.model.Account;
import com.yankov.backend.model.Transaction;
import com.yankov.backend.repository.TransactionRepository;
import com.yankov.backend.service.AccountService;
import com.yankov.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountService accountService;

    // Create raw transaction
    @Transactional
    @Override
    public Transaction createTransaction(Transaction transaction) {

        return transactionRepository.save(transaction);
    }

    // Deposit money
    @Transactional
    @Override
    public Transaction deposit(Account target, BigDecimal amount) {

        accountService.deposit(target, amount); //update balance

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .targetAccount(target)
                .build();

        return transactionRepository.save(transaction);
    }

    // Withdraw money
    @Transactional
    @Override
    public Transaction withdraw(Account source, BigDecimal amount) {
        accountService.withdraw(source, amount); // update balance

        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .sourceAccount(source)
                .build();

        return transactionRepository.save(transaction);
    }

    // Transfer money
    @Transactional
    @Override
    public Transaction transfer(Account source, Account target, BigDecimal amount) {

        accountService.withdraw(source, amount); // remove from source
        accountService.deposit(target, amount); // add to target

        Transaction transaction = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .sourceAccount(source)
                .targetAccount(target)
                .build();

        return transactionRepository.save(transaction);
    }
    // Get transaction where account is the source
    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getTransactionsBySourceAccount(Account account) {

        return transactionRepository.findBySourceAccount(account);
    }

    // Get transaction where account is the target
    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getTransactionsByTargetAccount(Account account) {

        return transactionRepository.findByTargetAccount(account);
    }
}

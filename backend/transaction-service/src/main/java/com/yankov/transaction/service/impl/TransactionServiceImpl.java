package com.yankov.transaction.service.impl;

import com.yankov.transaction.client.AccountClient;
import com.yankov.transaction.enums.TransactionStatus;
import com.yankov.transaction.enums.TransactionType;
import com.yankov.transaction.model.Transaction;
import com.yankov.transaction.repository.TransactionRepository;
import com.yankov.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountClient accountClient;

    // Create raw transaction
    @Transactional
    @Override
    public Transaction createTransaction(Transaction transaction) {

        return transactionRepository.save(transaction);
    }

    // Deposit money into target account
    @Transactional
    @Override
    public Transaction deposit(String accountNumber, BigDecimal amount) {

        // inform account-service over the network to mutate the balance record
        accountClient.executeInternalDeposit(accountNumber, amount);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .targetAccountNumber(accountNumber)
                .build();

        return transactionRepository.save(transaction);
    }

    // Withdraw money from source account
    @Transactional
    @Override
    public Transaction withdraw(String accountNumber, BigDecimal amount) {

        // inform account-service over the network to execute safety checks and withdraw
        accountClient.executeInternalWithdraw(accountNumber, amount);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .sourceAccountNumber(accountNumber)
                .build();

        return transactionRepository.save(transaction);
    }

    // Transfer money between two accounts
    @Transactional
    @Override
    public Transaction transfer(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount) {

        // request currency validation check from account-service over the network
        accountClient.verifyCurrencyMatch(sourceAccountNumber, targetAccountNumber);

        // Atomic operation: if either fails, whole transaction is rolled back
        accountClient.executeInternalWithdraw(sourceAccountNumber, amount); // remove from source
        accountClient.executeInternalDeposit(targetAccountNumber, amount); // add to target

        Transaction transaction = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .amount(amount)
                .sourceAccountNumber(sourceAccountNumber)
                .targetAccountNumber(targetAccountNumber)
                .build();

        return transactionRepository.save(transaction);
    }
    // Get transaction where account is the source
    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getTransactionsBySourceAccountNumber(String sourceAccountNumber) {

        return transactionRepository.findBySourceAccountNumber(sourceAccountNumber);
    }

    // Get transaction where account is the target
    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getTransactionsByTargetAccountNumber(String targetAccountNumber) {

        return transactionRepository.findByTargetAccountNumber(targetAccountNumber);
    }
}

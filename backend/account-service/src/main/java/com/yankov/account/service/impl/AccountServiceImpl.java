package com.yankov.account.service.impl;

import com.yankov.account.client.UserClient;
import com.yankov.account.enums.Currency;
import com.yankov.account.exception.AccountNotFoundException;
import com.yankov.account.exception.InsufficientBalanceException;
import com.yankov.account.exception.InvalidTransactionException;
import com.yankov.account.model.Account;
import com.yankov.account.repository.AccountRepository;
import com.yankov.account.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final UserClient userClient;

    private static final BigDecimal AMOUNT_ZERO = BigDecimal.ZERO;

    // create account
    @Transactional
    @Override
    public Account createAccountByEmail(String email, Currency currency) {

        // call auth-service microservice over the network to get the ID number
        Long userId = userClient.getUserIdByEmail(email);

        Account account = Account.builder()
                .userId(userId)
                .currency(currency)
                .balance(AMOUNT_ZERO) // always start at zero
                .accountNumber(generateAccountNumber())
                .build();

        return accountRepository.save(account);
    }

    // helper method to generate unique account number
    private String generateAccountNumber() {

        return "ACC" + UUID.randomUUID()
                .toString()
                .replaceAll("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    // get account by the account number
    @Transactional(readOnly = true)
    @Override
    public Account getAccountByAccountNumber(String number) {

        return accountRepository
                .findByAccountNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }

    // get accounts by the user
    @Transactional(readOnly = true)
    @Override
    public List<Account> getAccountsByEmail(String email) {

        Long userId = userClient.getUserIdByEmail(email);

        return accountRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public void deposit(Account account, BigDecimal amount) {

        // prevent invalid deposit amount 0 or negative
        if (amount.compareTo(AMOUNT_ZERO) <= 0) {
            throw new InvalidTransactionException(AMOUNT_ZERO);
        }
        account.setBalance(account.getBalance().add(amount));

        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void withdraw(Account account, BigDecimal amount) {

        // prevent invalid withdrawal amounts 0 or negative
        if (amount.compareTo(AMOUNT_ZERO) <= 0) {
            throw new InvalidTransactionException(AMOUNT_ZERO);
        }

        // prevent withdrawing more money than the account holds
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    account.getId(), amount
            );
        }

        account.setBalance(account.getBalance().subtract(amount));

        accountRepository.save(account);
    }
}

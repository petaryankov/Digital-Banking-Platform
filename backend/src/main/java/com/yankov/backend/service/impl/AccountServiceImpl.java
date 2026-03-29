package com.yankov.backend.service.impl;

import com.yankov.backend.enums.Currency;
import com.yankov.backend.exception.AccountNotFoundException;
import com.yankov.backend.exception.InsufficientBalanceException;
import com.yankov.backend.exception.InvalidTransactionException;
import com.yankov.backend.model.Account;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.AccountRepository;
import com.yankov.backend.service.AccountService;
import com.yankov.backend.service.UserService;
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

    private final UserService userService;

    private static final BigDecimal AMOUNT_ZERO = BigDecimal.ZERO;

    // Create account
    @Transactional
    @Override
    public Account createAccountByEmail(String email, Currency currency) {

        Account account = Account.builder()
                .user(userService.getUserByEmail(email))
                .currency(currency)
                .balance(AMOUNT_ZERO) // always start at zero
                .accountNumber(generateAccountNumber())
                .build();

        return accountRepository.save(account);
    }

    // Helper method to generate unique account number
    private String generateAccountNumber() {

        return "ACC" + UUID.randomUUID()
                .toString()
                .replaceAll("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    // Get account by the account number
    @Transactional(readOnly = true)
    @Override
    public Account getAccountByAccountNumber(String number) {

        return accountRepository
                .findByAccountNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }

    // Get accounts by the user
    @Transactional(readOnly = true)
    @Override
    public List<Account> getAccountsByEmail(String email) {

        User user = userService.getUserByEmail(email);

        return accountRepository.findByUser(user);
    }

    @Transactional
    @Override
    public void deposit(Account account, BigDecimal amount) {

        // Prevent invalid deposit amount 0 or negative
        if (amount.compareTo(AMOUNT_ZERO) <= 0) {
            throw new InvalidTransactionException(AMOUNT_ZERO);
        }
        account.setBalance(account.getBalance().add(amount));

        accountRepository.save(account);
    }

    @Transactional
    @Override
    public void withdraw(Account account, BigDecimal amount) {

        // Prevent invalid withdrawal amounts 0 or negative
        if (amount.compareTo(AMOUNT_ZERO) <= 0) {
            throw new InvalidTransactionException(AMOUNT_ZERO);
        }

        // Prevent withdrawing more money than the account holds
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    account.getId(), amount
            );
        }

        account.setBalance(account.getBalance().subtract(amount));

        accountRepository.save(account);
    }
}

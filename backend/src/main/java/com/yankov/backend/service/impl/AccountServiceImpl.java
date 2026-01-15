package com.yankov.backend.service.impl;

import com.yankov.backend.model.Account;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.AccountRepository;
import com.yankov.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public Account createAccount(Account account) {

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Account> getAccountByNumber(String number) {
        return accountRepository.findByAccountNumber(number);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Account> getAccountByUser(User user) {

        return accountRepository.findByUser(user);
    }

    @Transactional
    @Override
    public Account deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));

        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public Account withdraw(Account account, BigDecimal amount) {

        account.setBalance(account.getBalance().subtract(amount));

        return accountRepository.save(account);
    }
}

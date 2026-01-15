package com.yankov.backend.service;

import com.yankov.backend.model.Account;
import com.yankov.backend.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account createAccount(Account account);

    Optional<Account> getAccountByNumber(String number);

    List<Account> getAccountByUser(User user);

    Account deposit(Account account, BigDecimal amount);

    Account withdraw(Account account, BigDecimal amount);
}

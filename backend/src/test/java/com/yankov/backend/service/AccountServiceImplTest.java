package com.yankov.backend.service;

import com.yankov.backend.enums.Currency;
import com.yankov.backend.exception.AccountNotFoundException;
import com.yankov.backend.exception.InsufficientBalanceException;
import com.yankov.backend.exception.InvalidTransactionException;
import com.yankov.backend.model.Account;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.AccountRepository;
import com.yankov.backend.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private static final Long USER_ID = 1L;
    private static final Long ACCOUNT_ID = 10L;
    private static final String ACCOUNT_NUMBER = "ACC123456";
    private static final String USER_FULL_NAME = "John Doe";
    private static final String USER_EMAIL = "john@test.com";
    private static final BigDecimal AMOUNT_ZERO= BigDecimal.ZERO;
    private static final BigDecimal AMOUNT_50 = new BigDecimal("50.00");
    private static final BigDecimal AMOUNT_100 = new BigDecimal("100.00");
    private static final BigDecimal AMOUNT_150 = new BigDecimal("150.00");
    private static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .fullName(USER_FULL_NAME)
                .email(USER_EMAIL)
                .build();

        account = Account.builder()
                .id(ACCOUNT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .currency(Currency.EUR)
                .balance(AMOUNT_ZERO)
                .user(user)
                .build();
    }

    // Create account with zero balance linked to the correct user
    @Test
    void shouldCreateAccountWithZeroBalance() {

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createAccount(USER_ID, Currency.EUR);

        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getCurrency()).isEqualTo(Currency.EUR);
        assertThat(result.getBalance()).isEqualByComparingTo(AMOUNT_ZERO);
        assertThat(result.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);

        verify(accountRepository).save(any(Account.class));
    }

    // Successful retrieval of an account by its unique account number
    @Test
    void shouldReturnAccountByAccountNumber() {

        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER))
                .thenReturn(Optional.of(account));

        Account result = accountService.getAccountByAccountNumber(ACCOUNT_NUMBER);

        assertThat(result).isEqualTo(account);
    }

    // Exception is thrown when an account number does not exist
    @Test
    void shouldThrowExceptionWhenAccountNotFound() {

        when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                accountService.getAccountByAccountNumber(ACCOUNT_NUMBER)
        ).isInstanceOf(AccountNotFoundException.class);
    }

    // All accounts belonging to a user are returned
    @Test
    void shouldReturnAccountsByUserId() {

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(accountRepository.findByUser(user))
                .thenReturn(List.of(account));

        List<Account> result = accountService.getAccountsByUserId(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(account);
    }

    // Valid deposit increases the account balance correctly
    @Test
    void shouldDepositMoney() {

        accountService.deposit(account, AMOUNT_50);

        assertThat(account.getBalance())
                .isEqualByComparingTo(AMOUNT_50);

        verify(accountRepository).save(account);
    }

    // Prevents deposits with zero or negative amounts
    @Test
    void shouldThrowExceptionWhenDepositAmountIsInvalid() {

        assertThatThrownBy(() ->
                accountService.deposit(account, BigDecimal.ZERO)
        ).isInstanceOf(InvalidTransactionException.class);

        verify(accountRepository, never()).save(any());
    }

    // Withdrawal reduces the balance when sufficient funds exist
    @Test
    void shouldWithdrawMoney() {

        account.setBalance(AMOUNT_200);

        accountService.withdraw(account, AMOUNT_50);

        assertThat(account.getBalance())
                .isEqualByComparingTo(AMOUNT_150);

        verify(accountRepository).save(account);
    }

    // Prevents withdrawals when the account has insufficient balance
    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {

        account.setBalance(AMOUNT_100);

        assertThatThrownBy(() ->
                accountService.withdraw(account, AMOUNT_200)
        ).isInstanceOf(InsufficientBalanceException.class);

        verify(accountRepository, never()).save(any());
    }

    // Prevents withdrawals with zero or negative amounts
    @Test
    void shouldThrowExceptionWhenWithdrawAmountIsInvalid() {

        assertThatThrownBy(() ->
                accountService.withdraw(account, AMOUNT_ZERO)
        ).isInstanceOf(InvalidTransactionException.class);
    }
}

package com.yankov.backend.service;

import com.yankov.backend.enums.Currency;
import com.yankov.backend.enums.TransactionStatus;
import com.yankov.backend.enums.TransactionType;
import com.yankov.backend.exception.CurrencyMismatchException;
import com.yankov.backend.model.Account;
import com.yankov.backend.model.Transaction;
import com.yankov.backend.repository.TransactionRepository;
import com.yankov.backend.service.impl.TransactionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account sourceAccount;
    private Account targetAccount;

    private static final Long ACCOUNT_ID = 1L;
    private static final String SOURCE_ACCOUNT_NUMBER = "ACC123";
    private static final String TARGET_ACCOUNT_NUMBER = "ACC456";

    private static final BigDecimal AMOUNT_10 = BigDecimal.valueOf(10);
    private static final BigDecimal AMOUNT_30 = BigDecimal.valueOf(30);
    private static final BigDecimal AMOUNT_40 = BigDecimal.valueOf(40);
    private static final BigDecimal AMOUNT_50 = BigDecimal.valueOf(50);
    private static final BigDecimal AMOUNT_100 = BigDecimal.valueOf(100);

    @BeforeEach
    public void setUp() {

        // Initialize source and target accounts
        sourceAccount = Account.builder()
                .id(ACCOUNT_ID)
                .accountNumber(SOURCE_ACCOUNT_NUMBER)
                .currency(Currency.EUR)
                .balance(AMOUNT_100)
                .build();

        targetAccount = Account.builder()
                .id(ACCOUNT_ID)
                .accountNumber(TARGET_ACCOUNT_NUMBER)
                .currency(Currency.EUR)
                .balance(AMOUNT_50)
                .build();
    }

    // Completed deposit transaction with updated account balance
    @Test
    public void deposit_shouldCreateCompletedTransaction() {

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result = transactionService.deposit(targetAccount, AMOUNT_100);

        assertThat(result.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(AMOUNT_100);
        assertThat(result.getTargetAccount()).isEqualTo(targetAccount);

        verify(accountService).deposit(targetAccount, AMOUNT_100);
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Completed withDraw transaction with updated account balance
    @Test
    void withdraw_shouldCreateCompletedTransaction() {

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.withdraw(sourceAccount, AMOUNT_30);

        assertThat(result.getType()).isEqualTo(TransactionType.WITHDRAW);
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(AMOUNT_30);
        assertThat(result.getSourceAccount()).isEqualTo(sourceAccount);

        verify(accountService).withdraw(sourceAccount, AMOUNT_30);
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Transfer with withdraw from source account and deposit to target account
    @Test
    void transfer_shouldMoveMoneyAndCreateTransaction() {

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.transfer(
                sourceAccount, targetAccount, AMOUNT_40
        );

        assertThat(result.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(AMOUNT_40);
        assertThat(result.getSourceAccount()).isEqualTo(sourceAccount);
        assertThat(result.getTargetAccount()).isEqualTo(targetAccount);

        verify(accountService).withdraw(sourceAccount, AMOUNT_40);
        verify(accountService).deposit(targetAccount, AMOUNT_40);
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Prevents transfers between accounts with different currencies
    @Test
    void transfer_shouldThrowException_whenCurrenciesMismatch() {
        targetAccount.setCurrency(Currency.USD);

        assertThatThrownBy(() ->
                transactionService.transfer(sourceAccount, targetAccount, AMOUNT_10)
        ).isInstanceOf(CurrencyMismatchException.class);

        verify(accountService, never()).withdraw(any(), any());
        verify(accountService, never()).deposit(any(), any());
        verify(transactionRepository, never()).save(any());
    }

    // Retrieves all transactions where the account is the source
    @Test
    void getTransactionsBySourceAccount_shouldReturnList() {
        List<Transaction> transactions = List.of(new Transaction());

        when(transactionRepository.findBySourceAccount(sourceAccount))
                .thenReturn(transactions);

        List<Transaction> result =
                transactionService.getTransactionsBySourceAccount(sourceAccount);

        assertThat(result).hasSize(1);
        verify(transactionRepository).findBySourceAccount(sourceAccount);
    }

    // Retrieves all transactions where the account is the target
    @Test
    void getTransactionsByTargetAccount_shouldReturnList() {
        List<Transaction> transactions = List.of(new Transaction());

        when(transactionRepository.findByTargetAccount(targetAccount))
                .thenReturn(transactions);

        List<Transaction> result =
                transactionService.getTransactionsByTargetAccount(targetAccount);

        assertThat(result).hasSize(1);
        verify(transactionRepository).findByTargetAccount(targetAccount);
    }

}

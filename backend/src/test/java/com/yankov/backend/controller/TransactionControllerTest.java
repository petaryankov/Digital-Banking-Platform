package com.yankov.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yankov.backend.enums.Currency;
import com.yankov.backend.enums.TransactionStatus;
import com.yankov.backend.enums.TransactionType;
import com.yankov.backend.model.Account;
import com.yankov.backend.model.Transaction;
import com.yankov.backend.model.dto.request.TransactionRequestDto;
import com.yankov.backend.model.dto.request.TransferRequestDto;
import com.yankov.backend.security.CustomUserDetailsService;
import com.yankov.backend.security.JwtService;
import com.yankov.backend.service.AccountService;
import com.yankov.backend.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Disable security filters until JWT is implemented
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Test constants
    private static final Long ACCOUNT_ID = 1L;
    private static final String SOURCE_ACCOUNT_NUMBER = "ACC123";
    private static final String TARGET_ACCOUNT_NUMBER = "ACC456";
    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";
    private static final String TRANSACTION_STATUS_COMPLETED = "COMPLETED";
    private static final BigDecimal AMOUNT_50 = BigDecimal.valueOf(50);
    private static final BigDecimal AMOUNT_100 = BigDecimal.valueOf(100);

    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
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

    // POST /deposit
    @Test
    void deposit_shouldReturnTransaction() throws Exception {

        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber(TARGET_ACCOUNT_NUMBER);
        request.setAmount(AMOUNT_100);

        Transaction transaction = Transaction.builder()
                .id(ACCOUNT_ID)
                .amount(AMOUNT_100)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .targetAccount(targetAccount)
                .created_At(LocalDateTime.now())
                .build();

        when(accountService.getAccountByAccountNumber(TARGET_ACCOUNT_NUMBER))
                .thenReturn(targetAccount);
        when(transactionService.deposit(targetAccount, AMOUNT_100))
                .thenReturn(transaction);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.type").value(TRANSACTION_TYPE_DEPOSIT))
                .andExpect(jsonPath("$.status").value(TRANSACTION_STATUS_COMPLETED))
                .andExpect(jsonPath("$.amount").value(AMOUNT_100))
                .andExpect(jsonPath("$.targetAccountNumber").value(TARGET_ACCOUNT_NUMBER));
    }

    // POST /withdraw
    @Test
    void withdraw_shouldReturnTransaction() throws Exception {

        // Arrange
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAccountNumber(SOURCE_ACCOUNT_NUMBER);
        request.setAmount(AMOUNT_50);

        Transaction transaction = Transaction.builder()
                .id(ACCOUNT_ID)
                .amount(AMOUNT_50)
                .type(TransactionType.WITHDRAW)
                .status(TransactionStatus.COMPLETED)
                .sourceAccount(sourceAccount)
                .created_At(LocalDateTime.now())
                .build();

        when(accountService.getAccountByAccountNumber(SOURCE_ACCOUNT_NUMBER))
                .thenReturn(sourceAccount);
        when(transactionService.withdraw(sourceAccount, AMOUNT_50))
                .thenReturn(transaction);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.type").value(TRANSACTION_TYPE_WITHDRAW))
                .andExpect(jsonPath("$.status").value(TRANSACTION_STATUS_COMPLETED))
                .andExpect(jsonPath("$.amount").value(AMOUNT_50))
                .andExpect(jsonPath("$.sourceAccountNumber").value(SOURCE_ACCOUNT_NUMBER));
    }

    // POST /transfer
    @Test
    void transfer_shouldReturnTransaction() throws Exception {

        // Arrange
        TransferRequestDto request = new TransferRequestDto();
        request.setSourceAccountNumber(SOURCE_ACCOUNT_NUMBER);
        request.setTargetAccountNumber(TARGET_ACCOUNT_NUMBER);
        request.setAmount(AMOUNT_50);

        Transaction transaction = Transaction.builder()
                .id(ACCOUNT_ID)
                .amount(AMOUNT_50)
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .created_At(LocalDateTime.now())
                .build();

        when(accountService.getAccountByAccountNumber(SOURCE_ACCOUNT_NUMBER))
                .thenReturn(sourceAccount);
        when(accountService.getAccountByAccountNumber(TARGET_ACCOUNT_NUMBER))
                .thenReturn(targetAccount);
        when(transactionService.transfer(sourceAccount, targetAccount, AMOUNT_50))
                .thenReturn(transaction);

        // Act & Assert
        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.type").value(TRANSACTION_TYPE_TRANSFER))
                .andExpect(jsonPath("$.status").value(TRANSACTION_STATUS_COMPLETED))
                .andExpect(jsonPath("$.amount").value(AMOUNT_50))
                .andExpect(jsonPath("$.sourceAccountNumber").value(SOURCE_ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.targetAccountNumber").value(TARGET_ACCOUNT_NUMBER));
    }

    // GET /target
    @Test
    void getTransactionsByTargetAccount_shouldReturnList() throws Exception {

        // Arrange
        Transaction transaction = Transaction.builder()
                .id(ACCOUNT_ID)
                .amount(AMOUNT_50)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .targetAccount(targetAccount)
                .created_At(LocalDateTime.now())
                .build();

        when(accountService.getAccountByAccountNumber(TARGET_ACCOUNT_NUMBER))
                .thenReturn(targetAccount);
        when(transactionService.getTransactionsByTargetAccount(targetAccount))
                .thenReturn(List.of(transaction));

        // Act & Assert
        mockMvc.perform(get("/api/transactions/target")
                        .param("accountNumber", TARGET_ACCOUNT_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$[0].targetAccountNumber").value(TARGET_ACCOUNT_NUMBER));
    }
}

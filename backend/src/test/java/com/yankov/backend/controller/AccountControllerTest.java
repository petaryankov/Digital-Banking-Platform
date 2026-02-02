package com.yankov.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.yankov.backend.enums.Currency;
import com.yankov.backend.model.Account;
import com.yankov.backend.model.User;
import com.yankov.backend.model.dto.request.AccountCreateRequestDto;
import com.yankov.backend.security.CustomUserDetailsService;
import com.yankov.backend.security.JwtService;
import com.yankov.backend.service.AccountService;

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
@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock service layer dependency
    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Test constants
    private static final Long USER_ID = 1L;
    private static final Long ACCOUNT_ID = 10L;
    private static final String ACCOUNT_NUMBER = "ACC123456";
    private static final BigDecimal BALANCE_ZERO = BigDecimal.ZERO;
    private static final BigDecimal BALANCE_100 = BigDecimal.valueOf(100);
    private static final String CURRENCY_EURO = "EUR";

    // POST /api/accounts
    @Test
    void createAccount_shouldReturnCreatedAccount() throws Exception {

        // Arrange
        AccountCreateRequestDto request = AccountCreateRequestDto.builder()
                .userId(USER_ID)
                .currency(Currency.EUR)
                .build();

        User user = User.builder()
                .id(USER_ID)
                .build();

        Account savedAccount = Account.builder()
                .id(ACCOUNT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .balance(BigDecimal.ZERO)
                .currency(Currency.EUR)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountService.createAccount(USER_ID, Currency.EUR))
                .thenReturn(savedAccount);

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.balance").value(BALANCE_ZERO))
                .andExpect(jsonPath("$.currency").value(CURRENCY_EURO))
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    // GET /api/accounts/by-number
    @Test
    void getAccountByAccountNumber_shouldReturnAccount() throws Exception {

        // Arrange
        User user = User.builder()
                .id(USER_ID)
                .build();

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .balance(BALANCE_100)
                .currency(Currency.EUR)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountService.getAccountByAccountNumber(ACCOUNT_NUMBER))
                .thenReturn(account);

        // Act & Assert
        mockMvc.perform(get("/api/accounts/by-number")
                        .param("accountNumber", ACCOUNT_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.balance").value(BALANCE_100))
                .andExpect(jsonPath("$.currency").value(CURRENCY_EURO))
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    // GET /api/accounts/user/{userId}
    @Test
    void getAccountsByUser_shouldReturnAccountList() throws Exception {

        // Arrange
        User user = User.builder()
                .id(USER_ID)
                .build();

        Account account = Account.builder()
                .id(ACCOUNT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .balance(BALANCE_100)
                .currency(Currency.EUR)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountService.getAccountsByUserId(USER_ID))
                .thenReturn(List.of(account));

        // Act & Assert
        mockMvc.perform(get("/api/accounts/user/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(ACCOUNT_ID))
                .andExpect(jsonPath("$[0].accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$[0].currency").value(CURRENCY_EURO))
                .andExpect(jsonPath("$[0].userId").value(USER_ID));
    }
}
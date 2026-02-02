package com.yankov.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.yankov.backend.constants.ExceptionMessages;
import com.yankov.backend.exception.*;

import com.yankov.backend.security.CustomUserDetailsService;
import com.yankov.backend.security.JwtService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Disable security filters until JWT is implemented
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ExceptionTestController.class)
class GlobalExceptionHandlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the controller to trigger exceptions
    @MockitoBean
    private ExceptionTestController exceptionTestController;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Test constants
    private static final Long ACCOUNT_ID = 1L;
    private static final BigDecimal AMOUNT_ZERO = BigDecimal.ZERO;
    private static final BigDecimal AMOUNT_100 = BigDecimal.valueOf(100);
    private static final String USER_EMAIL = "test@example.com";
    private static final String ACCOUNT_NUMBER = "ACC123456";
    private static final String SOURCE_CURRENCY = "EUR";
    private static final String TARGET_CURRENCY = "USD";

    private static final String USER_NOT_FOUND_MESSAGE =
            String.format(ExceptionMessages.USER_NOT_FOUND_BY_EMAIL, USER_EMAIL);

    private static final String USER_ALREADY_EXISTS_MESSAGE =
            String.format(ExceptionMessages.USER_ALREADY_EXISTS, USER_EMAIL);

    private static final String ACCOUNT_NOT_FOUND_MESSAGE =
            String.format(ExceptionMessages.ACCOUNT_NOT_FOUND_BY_ACCOUNT_NUMBER, ACCOUNT_NUMBER);

    private static final String INSUFFICIENT_BALANCE_MESSAGE =
            String.format(ExceptionMessages.INSUFFICIENT_BALANCE, ACCOUNT_ID, AMOUNT_100);

    private static final String INVALID_TRANSACTION_MESSAGE =
            String.format(ExceptionMessages.INVALID_TRANSACTION_AMOUNT, AMOUNT_ZERO);

    private static final String CURRENCY_MISMATCH_MESSAGE =
            String.format(ExceptionMessages.CURRENCY_MISMATCH, SOURCE_CURRENCY, TARGET_CURRENCY);

    private static final String USER_NOT_FOUND_ENDPOINT = "/test/exceptions/user-not-found";
    private static final String ACCOUNT_NOT_FOUND_ENDPOINT = "/test/exceptions/account-not-found";
    private static final String INSUFFICIENT_BALANCE_ENDPOINT = "/test/exceptions/insufficient-balance";
    private static final String USER_ALREADY_EXISTS_ENDPOINT = "/test/exceptions/user-already-exists";
    private static final String INVALID_TRANSACTION_ENDPOINT = "/test/exceptions/invalid-transaction";
    private static final String CURRENCY_MISMATCH_ENDPOINT = "/test/exceptions/currency-mismatch";

    // GET /user-not-found
    @Test
    void userNotFoundException_shouldReturnNotFound() throws Exception {

        // Arrange: mock controller to throw UserNotFoundException
        doThrow(new UserNotFoundException(USER_EMAIL))
                .when(exceptionTestController)
                .userNotFound();

        // Act & Assert: expect 404 and correct message
        mockMvc.perform(get(USER_NOT_FOUND_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(USER_NOT_FOUND_MESSAGE));
    }

    // GET /user-already-exists
    @Test
    void userAlreadyExistsException_shouldReturnConflict() throws Exception {

        // Arrange: mock controller to throw UserAlreadyExistsException
        doThrow(new UserAlreadyExistsException(USER_EMAIL))
                .when(exceptionTestController)
                .userAlreadyExists();

        // Act & Assert: expect 409 and correct message
        mockMvc.perform(get(USER_ALREADY_EXISTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(USER_ALREADY_EXISTS_MESSAGE));
    }

    // GET /account-not-found
    @Test
    void accountNotFoundException_shouldReturnNotFound() throws Exception {

        // Arrange: mock controller to throw AccountNotFoundException
        doThrow(new AccountNotFoundException(ACCOUNT_NUMBER))
                .when(exceptionTestController)
                .accountNotFound();

        // Act & Assert: expect 404 and correct message
        mockMvc.perform(get(ACCOUNT_NOT_FOUND_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ACCOUNT_NOT_FOUND_MESSAGE));
    }

    // GET /insufficient-balance
    @Test
    void insufficientBalanceException_shouldReturnBadRequest() throws Exception {

        // Arrange: mock controller to throw InsufficientBalanceException
        doThrow(new InsufficientBalanceException(ACCOUNT_ID, AMOUNT_100))
                .when(exceptionTestController)
                .insufficientBalance();

        // Act & Assert: expect 400 and correct message
        mockMvc.perform(get(INSUFFICIENT_BALANCE_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INSUFFICIENT_BALANCE_MESSAGE));
    }

    // GET /invalid-transaction
    @Test
    void invalidTransactionException_shouldReturnBadRequest() throws Exception {

        // Arrange: mock controller to throw InvalidTransactionException
        doThrow(new InvalidTransactionException(AMOUNT_ZERO))
                .when(exceptionTestController)
                .invalidTransaction();

        // Act & Assert: expect 400 and correct message
        mockMvc.perform(get(INVALID_TRANSACTION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_TRANSACTION_MESSAGE));
    }

    // GET /currency-mismatch
    @Test
    void currencyMismatchException_shouldReturnBadRequest() throws Exception {

        // Arrange: mock controller to throw CurrencyMismatchException
        doThrow(new CurrencyMismatchException(SOURCE_CURRENCY, TARGET_CURRENCY))
                .when(exceptionTestController)
                .currencyMismatch();

        // Act & Assert: expect 400 and correct message
        mockMvc.perform(get(CURRENCY_MISMATCH_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CURRENCY_MISMATCH_MESSAGE));
    }
}

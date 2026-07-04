package com.yankov.account.controller;

import com.yankov.account.exception.CurrencyMismatchException;
import com.yankov.account.model.Account;
import com.yankov.account.model.dto.request.AccountCreateRequestDto;
import com.yankov.account.model.dto.response.AccountResponseDto;
import com.yankov.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Create account
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountCreateRequestDto request,
            Authentication authentication) {

        String email = authentication.getName();

        Account savedAccount = accountService
                .createAccountByEmail(email,
                        request.getCurrency());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(savedAccount));
    }

    // Find account by account number
    @GetMapping("/by-number")
    public ResponseEntity<AccountResponseDto> getAccountByAccountNumber(
            @RequestParam String accountNumber) {

        Account account = accountService
                .getAccountByAccountNumber(accountNumber);

        return ResponseEntity.ok(toResponse(account));
    }

    // Find All accounts for the user
    @GetMapping("/me")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByUser(
            Authentication authentication) {

        String email = authentication.getName();

        List<Account> accounts = accountService.getAccountsByEmail(email);

        List<AccountResponseDto> response = accounts.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // inbound route for transaction-service to execute an internal deposit
    @PostMapping("/internal/deposit")
    public ResponseEntity<Void> internalDeposit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        accountService.deposit(account, amount);

        return ResponseEntity.ok().build();
    }

    // inbound route for transaction-service to execute an internal withdraw
    @PostMapping("/internal/withdraw")
    public ResponseEntity<Void> internalWithdraw(@RequestParam String accountNumber,
                                                 @RequestParam BigDecimal amount) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        accountService.withdraw(account, amount);

        return ResponseEntity.ok().build();
    }

    // inbound route for transaction-service to verify currency match
    @PostMapping("/internal/check-currency-match")
    public ResponseEntity<Void> internalCheckCurrencyMatch(
            @RequestParam String sourceAccountNumber,
            @RequestParam String targetAccountNumber) {
        Account sourceAccount = accountService.getAccountByAccountNumber(sourceAccountNumber);
        Account targetAccount = accountService.getAccountByAccountNumber(targetAccountNumber);

        accountService.verifyCurrencyMatch(sourceAccountNumber, targetAccountNumber);

        return ResponseEntity.ok().build();
    }

    // private mapper
    private AccountResponseDto toResponse(Account account) {

        return AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .userId(account.getUserId())
                .createdAt(account.getCreatedAt())
                .build();
    }

}

package com.yankov.backend.controller;

import com.yankov.backend.model.Account;
import com.yankov.backend.model.dto.request.AccountCreateRequestDto;
import com.yankov.backend.model.dto.response.AccountResponseDto;
import com.yankov.backend.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Create account
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountCreateRequestDto request){

        Account savedAccount = accountService
                .createAccount(request.getUserId(),
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
    @GetMapping("user/{userId}")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByUser(
            @PathVariable Long userId) {

        List<Account> accounts = accountService.getAccountsByUserId(userId);

        List<AccountResponseDto> response = accounts.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // private mapper
    private AccountResponseDto toResponse(Account account) {

        return AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .userId(account.getUser().getId())
                .createdAt(account.getCreatedAt())
                .build();
    }

}

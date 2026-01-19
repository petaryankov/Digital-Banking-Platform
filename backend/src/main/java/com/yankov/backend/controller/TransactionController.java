package com.yankov.backend.controller;

import com.yankov.backend.model.Account;
import com.yankov.backend.model.Transaction;
import com.yankov.backend.model.dto.request.TransactionRequestDto;
import com.yankov.backend.model.dto.request.TransferRequestDto;
import com.yankov.backend.model.dto.response.TransactionResponseDto;
import com.yankov.backend.service.AccountService;
import com.yankov.backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final AccountService accountService;

    // Deposit money in to account
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDto> deposit(
            @Valid @RequestBody TransactionRequestDto request) {

        // Load target account by account number
        Account targetAccount = accountService
                .getAccountByAccountNumber(request.getAccountNumber());

        // Delegate to service
        Transaction transaction = transactionService
                .deposit(targetAccount, request.getAmount());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(transaction));

    }

    // Withdraw from account
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(
            @Valid @RequestBody TransactionRequestDto request
    ) {
        Account sourceAccount = accountService.getAccountByAccountNumber(request.getAccountNumber());

        Transaction transaction = transactionService.withdraw(sourceAccount, request.getAmount());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(transaction));
    }

    // Transfer between accounts
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(
            @Valid @RequestBody TransferRequestDto request
    ) {

        Account sourceAccount = accountService
                .getAccountByAccountNumber(request.getSourceAccountNumber());

        Account targetAccount = accountService
                .getAccountByAccountNumber(request.getTargetAccountNumber());

        Transaction transaction = transactionService.transfer(
                sourceAccount,
                targetAccount,
                request.getAmount()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(transaction));
    }

    // Get target account transactions
    @GetMapping("/target")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByTargetAccount(
            @RequestParam String accountNumber
    ) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);

        List<Transaction> transactions = transactionService.getTransactionsByTargetAccount(account);

        List<TransactionResponseDto> response = transactions.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }


    // Private mapper
    private TransactionResponseDto toResponse(Transaction transaction) {
        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .sourceAccountNumber(transaction.getSourceAccount() != null ?
                        transaction.getSourceAccount().getAccountNumber() : null)
                .targetAccountNumber(transaction.getTargetAccount() != null ?
                        transaction.getTargetAccount().getAccountNumber() : null)
                .createdAt(transaction.getCreated_At())
                .build();
    }
}

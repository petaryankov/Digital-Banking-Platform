package com.yankov.backend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto {

    @NotNull(message = "Source account number is required")
    private String sourceAccountNumber;

    @NotNull(message = "Target account number is required")
    private String targetAccountNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}

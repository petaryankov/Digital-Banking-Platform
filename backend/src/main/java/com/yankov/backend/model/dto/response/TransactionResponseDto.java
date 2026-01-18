package com.yankov.backend.model.dto.response;

import com.yankov.backend.enums.TransactionStatus;
import com.yankov.backend.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TransactionResponseDto {

    private Long id;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private String sourceAccountNumber;

    private String targetAccountNumber;

    private LocalDateTime createdAt;
}

package com.yankov.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AccountResponseDto {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private Long userId;

    private LocalDateTime createdAt;
}

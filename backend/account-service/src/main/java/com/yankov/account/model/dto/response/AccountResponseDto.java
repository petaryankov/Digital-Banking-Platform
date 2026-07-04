package com.yankov.account.model.dto.response;

import com.yankov.account.enums.Currency;
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

    private Currency currency;

    private Long userId;

    private LocalDateTime createdAt;
}

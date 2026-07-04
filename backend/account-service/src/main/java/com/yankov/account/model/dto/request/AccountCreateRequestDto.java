package com.yankov.account.model.dto.request;

import com.yankov.account.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCreateRequestDto {

    private Long userId;

    @NotNull(message = "Currency is required")
    private Currency currency;
}

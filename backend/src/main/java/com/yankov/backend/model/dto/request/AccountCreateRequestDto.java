package com.yankov.backend.model.dto.request;

import com.yankov.backend.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountCreateRequestDto {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Currency is required")
    private Currency currency;
}

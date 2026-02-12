package com.yankov.backend.util;

import com.yankov.backend.model.dto.request.TransactionRequestDto;
import com.yankov.backend.model.dto.request.TransferRequestDto;

import java.math.BigDecimal;

public final class TestRequestFactory {

    private TestRequestFactory() {}

    public static TransactionRequestDto depositRequest() {
        TransactionRequestDto dto = new TransactionRequestDto();
        dto.setAccountNumber("ACC123456");
        dto.setAmount(BigDecimal.TEN);
        return dto;
    }

    public static TransactionRequestDto withdrawRequest() {
        TransactionRequestDto dto = new TransactionRequestDto();
        dto.setAccountNumber("ACC123456");
        dto.setAmount(BigDecimal.TEN);
        return dto;
    }

    public static TransferRequestDto transferRequest() {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setSourceAccountNumber("ACC123456");
        dto.setTargetAccountNumber("ACC654321");
        dto.setAmount(BigDecimal.TEN);
        return dto;
    }
}

package com.yankov.transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service", url = "http://localhost:8082")
public interface AccountClient {

    @PostMapping("/api/accounts/interlnal/deposit")
    void executeInternalDeposit(@RequestParam("accountNumber") String accountNumber,
                                @RequestParam("amount") BigDecimal amount);
    @PostMapping("/api/accounts/internal/withdraw")
    void executeInternalWithdraw(@RequestParam("accountNumber") String accountNumber,
                                 @RequestParam("amount")  BigDecimal amount);

    @PostMapping("/api/accounts/internal/check-currency-match")
    void verifyCurrencyMatch(@RequestParam("sourceNumber") String sourceNumber,
                             @RequestParam("targetNumber") String targetNumber);
}

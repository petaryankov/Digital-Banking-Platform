package com.yankov.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "http://localhost:8081")
public interface UserClient {
    // matches UserController/AuthController lookup route in auth-service
    @GetMapping("/api/users/id-by-email")
    Long getUserIdByEmail(@RequestParam("email") String email);
}

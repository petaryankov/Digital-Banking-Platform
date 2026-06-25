package com.yankov.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.yankov.auth.constants.SecurityConstants.ADMIN_DASHBOARD;
import static com.yankov.auth.constants.SecurityConstants.ADMIN_ONLY;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(ADMIN_ONLY)
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return ADMIN_DASHBOARD;
    }
}

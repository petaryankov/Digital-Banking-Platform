package com.yankov.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "/sql/security-test-clean.sql",
        "/sql/security-test-users.sql",
        "/sql/security-test-accounts.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AccountControllerSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String userToken;
    private String adminToken;

    @BeforeEach
    public void setUp() {
        userToken = jwtService.generateAccessToken("user@test.com");
        adminToken = jwtService.generateAccessToken("admin@test.com");

    }

    @Test
    void shouldReturn401_whenNoTokenProvided() throws Exception {

        mockMvc.perform(get("/api/accounts/user/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_whenTokenIsInvalid() throws Exception {

        mockMvc.perform(get("/api/accounts/user/1")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnAccounts_whenUserIsAuthenticated() throws Exception {

        mockMvc.perform(get("/api/accounts/user/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC123456"))
                .andExpect(jsonPath("$[0].currency").value("EUR"));
    }

    @Test
    void shouldAllowAdminAccess_whenAdminTokenProvided() throws Exception {

        mockMvc.perform(get("/api/accounts/user/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

}

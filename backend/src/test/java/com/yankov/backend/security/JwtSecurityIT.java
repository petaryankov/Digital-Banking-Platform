package com.yankov.backend.security;

import com.yankov.backend.constants.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "/sql/security-test-clean.sql",
        "/sql/security-test-users.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JwtSecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String userToken;

    private String adminToken;

    private final String ADMIN_DASHBOARD_URI = "/api/admin/dashboard";

    @BeforeEach
    void setupTokens() {

        // Generate fresh JWT tokens for test users loaded via SQL
        userToken = jwtService.generateAccessToken("user@test.com");
        adminToken = jwtService.generateAccessToken("admin@test.com");
    }

    //Access protected endpoint without token → 401 Unauthorized
    @Test
    void whenNoToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI))
                .andExpect(status().isUnauthorized());
    }

    // Access protected endpoint with invalid token → 401 Unauthorized
    @Test
    void whenInvalidToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    // USER accessing ADMIN endpoint → 403 Forbidden
    @Test
    void whenUserAccessesAdminEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                SecurityConstants.TOKEN_PREFIX + userToken
                        ))
                .andExpect(status().isForbidden());
    }

    // ADMIN accessing ADMIN endpoint → 200 OK
    @Test
    void whenAdminAccessesAdminEndpoint_thenOk() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                SecurityConstants.TOKEN_PREFIX + adminToken
                        ))
                .andExpect(status().isOk());
    }
}

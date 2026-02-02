package com.yankov.backend.security;

import com.yankov.backend.BackendApplication;
import com.yankov.backend.constants.SecurityConstants;
import com.yankov.backend.enums.Role;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;

    private String adminToken;

    private final String ADMIN_DASHBOARD_URI = "/api/admin/dashboard";

    @BeforeAll
    void setupUsers() throws Exception {

        String USER_EMAIL = "user@test.com";
        String USER_FULL_NAME = "User Test";
        String USER_PASSWORD = "password";

        // Create USER
        User user = User.builder()
                .fullName(USER_FULL_NAME)
                .email(USER_EMAIL)
                .password(passwordEncoder.encode(USER_PASSWORD))
                .role(Role.USER)
                .build();

        String ADMIN_FULL_NAME = "Admin Test";
        String ADMIN_EMAIL = "admin@test.com";
        String ADMIN_PASSWORD = "password";

        // Create ADMIN
        User admin = User.builder()
                .fullName(ADMIN_FULL_NAME)
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
        userRepository.save(admin);

        userToken = jwtService.generateAccessToken(user.getEmail());
        adminToken = jwtService.generateAccessToken(admin.getEmail());
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
        String INVALID_TOKEN = "Bearer invalid.token.here";
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(HttpHeaders.AUTHORIZATION, INVALID_TOKEN))
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

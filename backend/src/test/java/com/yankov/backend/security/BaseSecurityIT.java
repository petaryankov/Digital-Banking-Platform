package com.yankov.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yankov.backend.BackendApplication;
import com.yankov.backend.model.RefreshToken;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.RefreshTokenRepository;
import com.yankov.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static com.yankov.backend.constants.SecurityConstants.TOKEN_PREFIX;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = BackendApplication.class)
@Sql(scripts = {
        "/sql/security-test-clean.sql",
        "/sql/security-test-users.sql",
        "/sql/security-test-accounts.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseSecurityIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;

    @Autowired
    protected UserRepository userRepository;

    // tokens for user/admin
    protected String userAccessToken;
    protected String adminAccessToken;
    protected String userRefreshToken;
    protected String adminRefreshToken;

    protected static final String USER_EMAIL = "user@test.com";
    protected static final String ADMIN_EMAIL = "admin@test.com";

    // setup tokens before each test
    @BeforeEach
    void setupTokens() {

        // generate access tokens
        userAccessToken = jwtService.generateAccessToken(USER_EMAIL);
        adminAccessToken = jwtService.generateAccessToken(ADMIN_EMAIL);

        // generate refresh tokens
        userRefreshToken = jwtService.generateRefreshToken(USER_EMAIL);
        adminRefreshToken = jwtService.generateRefreshToken(ADMIN_EMAIL);

        // create & save refresh token entities
        saveRefreshToken(userRefreshToken, USER_EMAIL);
        saveRefreshToken(adminRefreshToken, ADMIN_EMAIL);

    }

    // authorization header
    protected String bearer(String token) {
        return TOKEN_PREFIX + token;
    }


    // Save refresh token
    private void saveRefreshToken(String token, String email) {

        User user = userRepository.findByEmail(email).orElseThrow();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

}

package com.yankov.backend.security;

import com.yankov.backend.model.dto.request.RefreshTokenRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.yankov.backend.constants.ExceptionMessages.INVALID_TOKEN;
import static com.yankov.backend.constants.SecurityConstants.AUTHENTICATION_HEADER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthRefreshTokenSecurityIT extends BaseSecurityIT {

    private static final String REFRESH_URI = "/api/auth/refresh";

    // No token -> user is not authenticated -> 403
    @Test
    void shouldReturn403_whenNoTokenProvided() throws Exception {
            refreshWithoutToken().andExpect(status().isForbidden());
    }

    // Invalid token -> must be rejected -> 403
    @Test
    void shouldReturn403_whenTokenInvalid() throws Exception {
        refreshWithInvalidToken().andExpect(status().isForbidden());
    }

    // Access token is NOT allowed for refresh -> 403
    @Test
    void shouldReturn403_whenAccessTokenUsed() throws Exception {
        refreshWithAccessToken().andExpect(status().isForbidden());
    }

    // Valid refresh token -> new access token should be issued.
    @Test
    void shouldReturn200_andNewAccessToken_whenRefreshTokenValid() throws Exception {
        refreshAsUser().andExpect(status().isOk());
    }

    // Admin refresh token should work as well.
    @Test
    void shouldAllowAdminRefreshToken() throws Exception {
        refreshAsAdmin().andExpect(status().isOk());
    }

    // Revoked token -> must be rejected -> 403
    @Test
    void shouldReturn403_whenTokenRevoked() throws Exception {

        // revoke in DB
        refreshTokenRepository.findByToken(userRefreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });

        refreshAsUser()
                .andExpect(status().isForbidden());
    }

    // performs refresh call with given Authorization header and body
    private ResultActions refresh(String bearerToken, String body) throws Exception {
        MockHttpServletRequestBuilder request = post(REFRESH_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        if (bearerToken != null) {
            request.header(AUTHENTICATION_HEADER, bearerToken);
        }

        return mockMvc.perform(request);
    }

    // builds JSON request for refresh token
    private String refreshBody(String token) throws Exception {
        RefreshTokenRequestDto dto = RefreshTokenRequestDto.builder()
                .refreshToken(token)
                .build();

        return objectMapper.writeValueAsString(dto);
    }

    // valid user refresh
    private ResultActions refreshAsUser() throws Exception {
        return refresh(bearer(userRefreshToken), refreshBody(userRefreshToken));
    }

    // valid admin refresh
    private ResultActions refreshAsAdmin() throws Exception {
        return refresh(bearer(adminRefreshToken), refreshBody(adminRefreshToken));
    }

    // no header
    private ResultActions refreshWithoutToken() throws Exception {
        return refresh(null, "{}");
    }

    // garbage token
    private ResultActions refreshWithInvalidToken() throws Exception {
        return refresh(bearer(INVALID_TOKEN), refreshBody(INVALID_TOKEN));
    }

    // access token instead of refresh
    private ResultActions refreshWithAccessToken() throws Exception {
        return refresh(bearer(userAccessToken), refreshBody(userAccessToken));
    }

}

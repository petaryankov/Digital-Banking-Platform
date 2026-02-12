package com.yankov.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yankov.backend.util.TestRequestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.yankov.backend.constants.ExceptionMessages.INVALID_TOKEN;
import static com.yankov.backend.constants.SecurityConstants.AUTHENTICATION_HEADER;
import static com.yankov.backend.constants.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerSecurityIT extends BaseSecurityIT {

    private static final String DEPOSIT_URI = "/api/transactions/deposit";

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    // No token -> 401
    @Test
    void shouldReturn401_whenNoTokenProvided() throws Exception {
        mockMvc.perform(post(DEPOSIT_URI)
                        .contentType(APPLICATION_JSON)
                        .content(depositBody()))
                .andExpect(status().isUnauthorized());
    }

    // Invalid token -> 401
    @Test
    void shouldReturn401_whenTokenInvalid() throws Exception {
        mockMvc.perform(post(DEPOSIT_URI)
                .header(AUTHENTICATION_HEADER, TOKEN_PREFIX + INVALID_TOKEN)
                .contentType(APPLICATION_JSON)
                .content(depositBody()))
                .andExpect(status().isUnauthorized());
    }

    // USER token -> 201
    @Test
    void shouldAllowUser_whenAuthenticated() throws Exception {
        mockMvc.perform(post(DEPOSIT_URI)
                .header(AUTHENTICATION_HEADER, TOKEN_PREFIX + userAccessToken)
                .contentType(APPLICATION_JSON)
                .content(depositBody()))
                .andExpect(status().isCreated());
    }

    // ADMIN token -> 201
    @Test
    void shouldAllowAdmin_whenAuthenticated() throws Exception {
        mockMvc.perform(post(DEPOSIT_URI)
                .header(AUTHENTICATION_HEADER, TOKEN_PREFIX + adminAccessToken)
                .contentType(APPLICATION_JSON)
                .content(depositBody()))
                .andExpect(status().isCreated());
    }

    // USER refresh token -> 401
    @Test
    void shouldRejectUserRefreshToken() throws Exception {
        mockMvc.perform(post(DEPOSIT_URI)
                        .header(AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + userRefreshToken)
                        .contentType(APPLICATION_JSON)
                        .content(depositBody()))
                .andExpect(status().isUnauthorized());
    }

    // ADMIN refresh token -> 401
    @Test
    void shouldRejectAdminRefreshToken() throws Exception {
        mockMvc.perform(post(DEPOSIT_URI)
                        .header(AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + adminRefreshToken)
                        .contentType(APPLICATION_JSON)
                        .content(depositBody()))
                .andExpect(status().isUnauthorized());
    }

    private String depositBody() throws Exception {
        return objectMapper.writeValueAsString(
                TestRequestFactory.depositRequest());
    }
}
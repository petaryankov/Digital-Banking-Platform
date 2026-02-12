package com.yankov.backend.security;

import org.junit.jupiter.api.Test;

import static com.yankov.backend.constants.SecurityConstants.AUTHENTICATION_HEADER;
import static com.yankov.backend.constants.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AccountControllerSecurityIT extends BaseSecurityIT {

    private final String URI = "/api/accounts/user/1";

    @Test
    void shouldReturn401_whenNoAccessTokenProvided() throws Exception {

        mockMvc.perform(get(URI))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_whenAccessTokenIsInvalid() throws Exception {

        mockMvc.perform(get(URI)
                        .header(AUTHENTICATION_HEADER, TOKEN_PREFIX + "invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnAccounts_whenUserIsAuthenticated() throws Exception {

        mockMvc.perform(get(URI)
                        .header(AUTHENTICATION_HEADER, TOKEN_PREFIX + userAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC123456"))
                .andExpect(jsonPath("$[0].currency").value("EUR"));
    }

    @Test
    void shouldAllowAdminAccess_whenAdminAccessTokenProvided() throws Exception {

        mockMvc.perform(get(URI)
                        .header(AUTHENTICATION_HEADER, TOKEN_PREFIX + adminAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401_whenRefreshTokenUsedInsteadOfAccessToken_user() throws Exception {
        mockMvc.perform(get(URI)
                        .header(
                                AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + userRefreshToken
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_whenRefreshTokenUsedInsteadOfAccessToken_admin() throws Exception {
        mockMvc.perform(get(URI)
                        .header(
                                AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + adminRefreshToken
                        ))
                .andExpect(status().isUnauthorized());
    }

}

package com.yankov.backend.security;

import com.yankov.backend.constants.SecurityConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static com.yankov.backend.constants.ExceptionMessages.INVALID_TOKEN;
import static com.yankov.backend.constants.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthAccessTokenSecurityIT extends BaseSecurityIT {


    private final String ADMIN_DASHBOARD_URI = "/api/admin/dashboard";
    private final String USER_ENDPOINT = "/api/accounts/user/1";

    // Invalid token -> 401 Unauthorized
    @Test
    void whenNoToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI))
                .andExpect(status().isUnauthorized());
    }

    // Access protected endpoint with invalid token → 401 Unauthorized
    @Test
    void whenInvalidToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + INVALID_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    // USER role must not access ADMIN endpoint -> 403 Forbidden
    @Test
    void whenUserAccessesAdminEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + userAccessToken
                        ))
                .andExpect(status().isForbidden());
    }

    // ADMIN role must not access USER endpoint -> 403 Forbidden
    @Test
    void whenAdminAccessesUserEndpoint_thenOk() throws Exception {
        mockMvc.perform(get(USER_ENDPOINT)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + adminAccessToken
                        ))
                .andExpect(status().isOk());
    }

    // ADMIN role should access ADMIN endpoint -> 200 OK
    @Test
    void whenAdminAccessesAdminEndpoint_thenOk() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + adminAccessToken
                        ))
                .andExpect(status().isOk());
    }

    // USER role should access USER endpoint -> 200 OK
    @Test
    void whenUserAccessesUserEndpoint_thenOk() throws Exception {
        mockMvc.perform(get(USER_ENDPOINT)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + userAccessToken
                        ))
                .andExpect(status().isOk());
    }

    // Refresh token must NOT be accepted as access token -> 401 Unauthorized
    @Test
    void whenUserRefreshTokenUsed_thenUnauthorized() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + userRefreshToken
                        ))
                .andExpect(status().isUnauthorized());
    }

    // Refresh token must NOT be accepted as access token -> 401 Unauthorized
    @Test
    void whenAdminRefreshTokenUsed_thenUnauthorized() throws Exception {
        mockMvc.perform(get(ADMIN_DASHBOARD_URI)
                        .header(
                                SecurityConstants.AUTHENTICATION_HEADER,
                                TOKEN_PREFIX + adminRefreshToken
                        ))
                .andExpect(status().isUnauthorized());
    }
}

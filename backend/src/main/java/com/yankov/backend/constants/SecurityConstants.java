package com.yankov.backend.constants;

public class SecurityConstants {
    private SecurityConstants() {}

    // Roles
    public static final String ROLE_PREFIX = "ROLE_";

    // Authorization expression
    public static final String USER_OR_ADMIN =
            "hasAnyRole('USER','ADMIN')";
    public static final String ADMIN_ONLY =
            "hasRole('ADMIN')";
    public static final String USER_PROFILE_DATA =
            "User profile data";
    public static final String ADMIN_DASHBOARD =
            "Admin dashboard";

    // Endpoints
    public static final String AUTH_ENDPOINT = "/api/auth/**";

    // HTTP
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHENTICATION_HEADER = "Authorization";

}

package com.yankov.auth.constants;

public class ExceptionMessages {

    private ExceptionMessages() {}

    // USER
    public static final String USER_NOT_FOUND_BY_ID = "User not found with id %d";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User with email %s not found";
    public static final String USER_ALREADY_EXISTS = "User already exists with email %s";
    public static final String USER_IS_DEACTIVATED = "User is deactivated";

    // JWT
    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found";
    public static final String REFRESH_TOKEN_INVALID = "Refresh token is invalid or expired";
    public static final String INVALID_ISSUER =
            "Invalid JWT issuer";
    public static final String INVALID_TOKEN =
            "Invalid JWT token type";
    public static final String TOKEN_EXPIRED =
            "JWT token has expired";
    public static final String MALFORMED_TOKEN =
            "Malformed or unsupported JWT token";
    public static final String ACCESS_DENIED = "Access Denied";
    public static final String UNAUTHORIZED =
            "Authentication is required to access this resource";
    public static final String INVALID_CREDENTIALS =
            "Invalid email or password";
    public static final String UNEXPECTED_SERVER_ERROR = "Unexpected server error";

}

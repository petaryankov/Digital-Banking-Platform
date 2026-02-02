package com.yankov.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.yankov.backend.constants.ExceptionMessages.UNAUTHORIZED;
import static com.yankov.backend.constants.SecurityConstants.AUTHENTICATION_HEADER;

//Handles when JWT is missing, invalid, or expired (HTTP 401)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        securityErrorResponseWriter.write(
                request,
                response,
                HttpStatus.UNAUTHORIZED,
                UNAUTHORIZED
        );
    }

}

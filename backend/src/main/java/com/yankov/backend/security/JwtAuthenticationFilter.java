package com.yankov.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.yankov.backend.constants.JwtConstants.ACCESS_TOKEN_TYPE;
import static com.yankov.backend.constants.SecurityConstants.*;

// Executed once per request to authenticate users by token
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        // Read Authorization header ("Bearer eyJhbGciOiJIUzI1NiIs...")
        String authHeader = request.getHeader(AUTHENTICATION_HEADER);

        // If header is missing or does not start with expected prefix, skip JWT processing
        if (authHeader == null || !authHeader
                .startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract raw JWT token by removing "Bearer " prefix
        String token = authHeader.substring(TOKEN_PREFIX.length());

        Claims claims = jwtService
                .validateAndParse(token, ACCESS_TOKEN_TYPE);

        String email = claims.getSubject();

        // Process if token contains username and no authentication is already present in SecurityContext
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from database
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

                // Create authenticated token with user authorities
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Store authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);

        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    // Skip JWT filter for authentication endpoints (/api/auth/login)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith(AUTH_ENDPOINT);
    }

}

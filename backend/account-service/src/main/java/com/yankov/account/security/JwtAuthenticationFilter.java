package com.yankov.account.security;

import com.yankov.account.exception.InvalidJwtTokenException;
import com.yankov.account.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.yankov.account.constants.JwtConstants.ACCESS_TOKEN_TYPE;
import static com.yankov.account.constants.JwtConstants.TOKEN_ROLE;
import static com.yankov.account.constants.SecurityConstants.*;

// executed once per request to authenticate users by token
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        // read Authorization header ("Bearer eyJhbGciOiJIUzI1NiIs...")
        String authHeader = request.getHeader(AUTHENTICATION_HEADER);

        // if header is missing or does not start with expected prefix, skip JWT processing
        if (authHeader == null || !authHeader
                .startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract raw JWT token by removing "Bearer " prefix
        String token = authHeader.substring(TOKEN_PREFIX.length());

        try {

        Claims claims = jwtService
                .validateAndParse(token, ACCESS_TOKEN_TYPE);

        String email = claims.getSubject();

        // process if token contains username and no authentication is already present in SecurityContext
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // extract the users role form the JWT
            String role = claims.get(TOKEN_ROLE, String.class);

            if (role == null) {
                role = ROLE_USER;
            }

            UsernamePasswordAuthenticationToken authToken = getUsernamePasswordAuthenticationToken(role, email);

            // store authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);

        }
        } catch (InvalidJwtTokenException ex) {
            // AuthenticationEntryPoint handle it
            SecurityContextHolder.clearContext();
        }
        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    @Nonnull
    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String role, String email) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        // build a native Spring Security User object using data inside the token
        UserDetails userDetails = new User(email, "", authorities);

        // create authenticated token with user authorities
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    // skip JWT filter for authentication endpoints (/api/auth/login)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith(AUTH_ENDPOINT);
    }

}

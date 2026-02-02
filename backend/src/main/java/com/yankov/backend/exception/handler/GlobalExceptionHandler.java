package com.yankov.backend.exception.handler;

import com.yankov.backend.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.yankov.backend.constants.ExceptionMessages.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildResponseEntity(
            HttpStatus status,
            String message
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }

    // User exceptions

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException ex
    ) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(
            UserAlreadyExistsException ex
    ) {
        return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Account exceptions

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(
            AccountNotFoundException ex
    ) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalance(
            InsufficientBalanceException ex
    ) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Transaction exceptions

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTransaction(
            InvalidTransactionException ex
    ) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleCurrencyMismatch(
            CurrencyMismatchException ex
    ) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // JWT / Security

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex
    ) {
        return buildResponseEntity(
                HttpStatus.UNAUTHORIZED,
                INVALID_CREDENTIALS);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            AuthenticationException ex
    ) {
        return buildResponseEntity(
                HttpStatus.UNAUTHORIZED,
                UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJwtToken(
            InvalidJwtTokenException ex
    ) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex
    ) {
        return buildResponseEntity(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRefreshTokenNotFound(
            RefreshTokenNotFoundException ex
    )  {
        return buildResponseEntity(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex
    )  {
        return buildResponseEntity(HttpStatus.FORBIDDEN, ACCESS_DENIED);
    }

    // Fallback for all other RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex
    ) {
        return buildResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                UNEXPECTED_SERVER_ERROR
        );
    }

}

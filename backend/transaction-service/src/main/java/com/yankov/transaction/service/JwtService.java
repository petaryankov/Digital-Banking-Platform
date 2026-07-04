package com.yankov.transaction.service;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String extractTokenType(Claims claims);

    Claims validateAndParse(String token, String expectedType);
}



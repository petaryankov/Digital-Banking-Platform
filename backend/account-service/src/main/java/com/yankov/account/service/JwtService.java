package com.yankov.account.service;

import io.jsonwebtoken.Claims;

public interface JwtService {

    Claims validateAndParse(String token, String expectedType);
}



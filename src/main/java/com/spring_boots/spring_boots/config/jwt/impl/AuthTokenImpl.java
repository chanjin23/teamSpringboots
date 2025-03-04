package com.spring_boots.spring_boots.config.jwt.impl;


import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Optional;

@Getter
@AllArgsConstructor
@Slf4j
public class AuthTokenImpl {
    private final String token;
    private final Key key;

    public AuthTokenImpl(
            String userId,
            Key key,
            Claims claims
    ) {
        this.key = key;
        this.token = createJwtToken(userId, claims).get();
    }

    private Optional<String> createJwtToken(
            String userId,
            Claims claims
    ) {
        return Optional.ofNullable(Jwts.builder()
                .setSubject(userId) //jwt 고유 식별정보
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
        );
    }
}

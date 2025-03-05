package com.spring_boots.spring_boots.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 사용자 토큰 상수 관리
 */
@Component
public class JwtConstants {
    public static final String REFRESH_TOKEN_TYPE_VALUE = "refreshToken"; //리프레시 토큰 이름
    public static final String ACCESS_TOKEN_TYPE_VALUE = "accessToken"; //엑세스토큰이름
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);  //리프레시토큰 유호기간
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15); // 액세스 토큰 유효 기간
    public static int ACCESS_EXPIRED;
    public static int REFRESH_EXPIRED;
    public static int ACCESS_TOKEN_EXPIRED_COOKIE;
    public static int REFRESH_TOKEN_EXPIRED_COOKIE;
    public JwtConstants(
            @Value("${jwt.token.access-expires}") int accessExpired,
            @Value("${jwt.token.refresh-expires}") int refreshExpired
    ) {
        ACCESS_EXPIRED = accessExpired;
        REFRESH_EXPIRED = refreshExpired;
        ACCESS_TOKEN_EXPIRED_COOKIE = accessExpired / 1000;
        REFRESH_TOKEN_EXPIRED_COOKIE = refreshExpired / 1000;
    }
}

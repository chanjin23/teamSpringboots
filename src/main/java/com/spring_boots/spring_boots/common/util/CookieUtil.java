package com.spring_boots.spring_boots.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

import static com.spring_boots.spring_boots.config.jwt.JwtConstants.*;
import static com.spring_boots.spring_boots.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;

@Slf4j
@Component
public class CookieUtil {
    public static void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");

        if(name.equals(ACCESS_TOKEN_TYPE_VALUE) ||
                name.equals(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)) cookie.setMaxAge(ACCESS_TOKEN_EXPIRED_COOKIE);
        else if(name.equals(REFRESH_TOKEN_TYPE_VALUE)) cookie.setMaxAge(REFRESH_TOKEN_EXPIRED_COOKIE);

        cookie.setHttpOnly(true);  // HttpOnly 속성 설정
//        cookie.setSecure(true);  // Secure 속성 설정
        // SameSite 속성 설정: 쿠키를 CSRF 공격에 대해 보호하기 위해 Lax로 설정
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setHttpOnly(true);  // HttpOnly 속성 설정
//                cookie.setSecure(true);  // Secure 속성 설정
                cookie.setAttribute("SameSite", "Lax");
                response.addCookie(cookie);
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}

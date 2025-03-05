package com.spring_boots.spring_boots.config.jwt;

import com.spring_boots.spring_boots.common.util.CookieUtil;
import com.spring_boots.spring_boots.config.jwt.impl.JwtProviderImpl;
import com.spring_boots.spring_boots.user.dto.TokenDto;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.spring_boots.spring_boots.config.jwt.JwtConstants.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProviderImpl tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // JWT 토큰을 쿠키에서 추출
        String jwtAccessToken = resolveToken(request, ACCESS_TOKEN_TYPE_VALUE);
        String jwtRefreshToken = resolveToken(request, REFRESH_TOKEN_TYPE_VALUE);

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtAccessToken != null && tokenProvider.validateToken(jwtAccessToken)) {
            // 액세스토큰이 유효한 경우, Authentication 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(jwtAccessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwtRefreshToken != null) {
            // 액세스토큰이 만료된 경우, 리프레시토큰 검증
            log.info("Access token expired, validating refresh token...");

            // 리프레시토큰이 유효하다면 새로운 액세스토큰 발급
            // 해당 메소드에서 유효성 검사까지 같이 진행
            TokenDto tokenDto = tokenProvider.generateAccessTokenAndRefreshToken(jwtRefreshToken);

            log.info("토큰 재발급 완료..!");

            // 새로운 액세스토큰을 쿠키에 저장
            CookieUtil.addCookie(response, ACCESS_TOKEN_TYPE_VALUE, tokenDto.getAccessToken());
            CookieUtil.addCookie(response, REFRESH_TOKEN_TYPE_VALUE, tokenDto.getRefreshToken());

            // 새로운 액세스토큰으로 Authentication 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // 쿠키에서 토큰 추출
    private String resolveToken(HttpServletRequest request,String tokenType) {
        if (request.getCookies() == null) {
            return null;
        }

        // 쿠키에서 토큰 추출
        Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> tokenType.equals(cookie.getName()))
                .findFirst();

        return jwtCookie.map(Cookie::getValue).orElse(null);
    }

}

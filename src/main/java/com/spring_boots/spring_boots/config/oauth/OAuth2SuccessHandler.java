package com.spring_boots.spring_boots.config.oauth;

import com.spring_boots.spring_boots.common.util.CookieUtil;
import com.spring_boots.spring_boots.config.jwt.impl.JwtProviderImpl;
import com.spring_boots.spring_boots.user.domain.RefreshToken;
import com.spring_boots.spring_boots.user.domain.Users;
import com.spring_boots.spring_boots.user.repository.RefreshTokenRepository;
import com.spring_boots.spring_boots.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.spring_boots.spring_boots.config.jwt.JwtConstants.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 의존성 주입
    private final JwtProviderImpl provider;  // JWT 토큰 제공자
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository; // OAuth2 인증 요청 쿠키 저장소
    private final UserService userService; // 사용자 서비스

    // OAuth2 로그인 성공 시 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 인증된 사용자의 정보를 가져옴
        Users user = userService.findByUserRealId((String) oAuth2User.getAttributes().get("email")); // 사용자 정보 조회

        // 리프레시 토큰 생성 및 저장
        String refreshToken = provider.createAuthToken(
                user.getUserRealId(), user.getRole().getRoleName(), user.getUserId(), user.getProvider().getProvider(), REFRESH_TOKEN_TYPE_VALUE
        ).getToken();

        saveRefreshToken(user.getUserId(), refreshToken); // 리프레시 토큰 저장
        addRefreshTokenToCookie(request, response, refreshToken); // 리프레시 토큰을 쿠키에 추가

        // 액세스 토큰 생성
        String accessToken = provider.createAuthToken(
                user.getUserRealId(), user.getRole().getRoleName(), user.getUserId(), user.getProvider().getProvider(), ACCESS_TOKEN_TYPE_VALUE
        ).getToken();

        addAccessTokenToCookie(request, response, accessToken); // 리프레시 토큰을 쿠키에 추가
//        String targetUrl = getTargetUrl(accessToken); // 리다이렉트 URL 생성
//
        // 인증 관련 쿠키와 정보를 정리하고 리다이렉트 처리
        clearAuthenticationAttributes(request, response); // 인증 속성 정리
        getRedirectStrategy().sendRedirect(request, response, "/"); // 지정된 URL로 리다이렉트
    }

    // 엑세스 토큰을 쿠키에 추가하는 메서드
    private void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds(); // 쿠키 유효 기간 설정

        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_TYPE_VALUE); // 기존 쿠키 삭제
        CookieUtil.addCookie(response, ACCESS_TOKEN_TYPE_VALUE, accessToken); // 새 쿠키 추가
    }

    // 리프레시 토큰을 DB에 저장하는 메서드
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken)) // 기존 토큰이 있으면 업데이트
                .orElse(new RefreshToken(userId, newRefreshToken)); // 없으면 새로 생성

        refreshTokenRepository.save(refreshToken); // 저장소에 리프레시 토큰 저장
    }

    // 리프레시 토큰을 쿠키에 추가하는 메서드
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds(); // 쿠키 유효 기간 설정

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_TYPE_VALUE); // 기존 쿠키 삭제
        CookieUtil.addCookie(response, REFRESH_TOKEN_TYPE_VALUE, refreshToken); // 새 쿠키 추가
    }

    // 인증 관련 속성을 정리하는 메서드
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request); // 부모 클래스 메서드 호출하여 속성 정리
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response); // 인증 요청 쿠키 삭제
    }

    // 리다이렉트할 URL을 생성하는 메서드
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString("/") // 리다이렉트 경로 설정
                .queryParam("token", token) // URL에 액세스 토큰을 쿼리 파라미터로 추가
                .build()
                .toUriString();
    }

}

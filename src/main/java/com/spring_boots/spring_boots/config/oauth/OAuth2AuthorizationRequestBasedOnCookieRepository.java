package com.spring_boots.spring_boots.config.oauth;

import com.spring_boots.spring_boots.common.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // OAuth2 인증 요청을 저장할 때 사용하는 쿠키 이름
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    // 인증 요청을 제거하는 메서드, 여기서는 쿠키에서 해당 인증 요청을 불러온 후 반환함
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    // 쿠키에서 OAuth2AuthorizationRequest 를 로드하는 메서드
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        // 요청에 포함된 쿠키 중에서 이름이 OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME인 쿠키를 가져옴
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);

        // 가져온 쿠키를 OAuth2AuthorizationRequest 객체로 역직렬화하여 반환
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    // OAuth2 인증 요청을 쿠키에 저장하는 메서드
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        // authorizationRequest가 null인 경우, 쿠키에서 인증 요청 관련 쿠키를 제거함
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        // 인증 요청을 직렬화하여 쿠키에 저장하고 만료 시간을 설정함
        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest));
    }

    // 인증 요청 관련 쿠키를 제거하는 메서드
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키를 삭제하는 유틸리티 메서드 호출
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}

package com.spring_boots.spring_boots.user.controller;

import com.spring_boots.spring_boots.user.exception.UserNotFoundException;
import com.spring_boots.spring_boots.user.service.UserAuthService;
import org.springframework.test.context.ActiveProfiles;
import com.spring_boots.spring_boots.user.domain.UserRole;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenDto;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenLoginRequest;
import com.spring_boots.spring_boots.user.dto.response.JwtTokenResponse;
import com.spring_boots.spring_boots.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserAuthApiControllerTest {

    @Mock
    private UserService userService;  // UserService를 Mock으로 설정

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private HttpServletResponse response;  // HttpServletResponse를 Mock으로 설정

    @InjectMocks
    private UserAuthApiController userAuthApiController;  // 실제 테스트할 Controller 인스턴스

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }

    @Test
    @DisplayName("기존쿠키가 없을때 로그인")
    public void testJwtLoginNoCookie() {
        // 테스트를 위한 요청 및 응답 객체 생성
        JwtTokenLoginRequest loginRequest = new JwtTokenLoginRequest("testUser", "testPassword");

        JwtTokenDto jwtTokenDto = new JwtTokenDto("accessToken", "refreshToken", UserRole.USER);

        // Mock 동작 설정
        when(userAuthService.login(any(JwtTokenLoginRequest.class))).thenReturn(jwtTokenDto);

        // API 호출
        ResponseEntity<JwtTokenResponse> responseEntity = userAuthApiController.jwtLogin(
                loginRequest, response);  //쿠키가 없음!

        // 검증
        verify(userAuthService,
                times(1)).login(any(JwtTokenLoginRequest.class));  // UserService의 login 메서드가 호출되었는지 검증
        verify(response, times(2)).addCookie(any(Cookie.class));  // 토큰2개, 쿠키가 추가되었는지 검증

        // 응답 값 검증
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("accessToken", responseEntity.getBody().getAccessToken());
    }

    @Test
    @DisplayName("기존쿠키가 있을때 로그인")
    public void testJwtLoginYesCookie() {
        // 테스트용 쿠키 생성
        Cookie existingRefreshTokenCookie = new Cookie("refreshToken", "oldRefreshToken");

        // 테스트용 로그인 요청 및 응답 객체 생성
        JwtTokenLoginRequest loginRequest = new JwtTokenLoginRequest("testUser", "testPassword");

        JwtTokenDto jwtTokenDto = new JwtTokenDto("newAccessToken", "newRefreshToken", UserRole.USER);

        // Mock 동작 설정
        when(userAuthService.login(any(JwtTokenLoginRequest.class))).thenReturn(jwtTokenDto);   //로그인하면 jwtToken반환

        // API 호출
        ResponseEntity<JwtTokenResponse> responseEntity = userAuthApiController.jwtLogin(
                loginRequest, response);

        // 검증
        verify(userAuthService, times(1)).login(any(JwtTokenLoginRequest.class));  // UserService의 login 메서드가 호출되었는지 검증
        verify(response, times(4)).addCookie(any(Cookie.class));  //기존토큰 2개 삭제 및 추가 검증

        // 응답 값 검증
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("newAccessToken", responseEntity.getBody().getAccessToken());
    }

    @Test
    @DisplayName("로그인 실패시")
    public void loginFail() {
        // Given
        JwtTokenLoginRequest loginRequest = new JwtTokenLoginRequest("testUser", "testPassword");


        // Mock 동작 설정
        when(userAuthService.login(any(JwtTokenLoginRequest.class))).thenThrow(new UserNotFoundException("가입되지 않은 실제 ID 입니다."));

        // API 호출
        ResponseEntity<JwtTokenResponse> responseEntity = userAuthApiController.jwtLogin(
                loginRequest, response);  // 쿠키가 없음

        // 검증: userService의 login 메서드가 1번 호출되었는지 검증
        verify(userAuthService, times(1)).login(any(JwtTokenLoginRequest.class));

        // 리프레시 토큰,엑세스 토큰 두개  삭제했으므로 2번 실행
        verify(response, times(0)).addCookie(any(Cookie.class));

        // 응답 상태 코드가 404 NOT_FOUND 검증
        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity.getBody());  // 로그인 실패 시 응답 본문이 null 이 아님 검증
        assertEquals("가입되지 않은 실제 ID 입니다.",responseEntity.getBody().getMessage());

    }

}
package com.spring_boots.spring_boots.user.controller;

import com.spring_boots.spring_boots.user.domain.UserRole;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenDto;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenLoginRequest;
import com.spring_boots.spring_boots.user.dto.request.RefreshTokenRequest;
import com.spring_boots.spring_boots.user.dto.response.JwtTokenResponse;
import com.spring_boots.spring_boots.user.dto.response.RefreshTokenResponse;
import com.spring_boots.spring_boots.user.dto.response.UserValidateTokenResponseDto;
import com.spring_boots.spring_boots.user.exception.PasswordNotMatchException;
import com.spring_boots.spring_boots.user.exception.UserDeletedException;
import com.spring_boots.spring_boots.user.exception.UserNotFoundException;
import com.spring_boots.spring_boots.user.service.TokenService;
import com.spring_boots.spring_boots.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.spring_boots.spring_boots.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;
import static com.spring_boots.spring_boots.config.jwt.UserConstants.REFRESH_TOKEN_TYPE_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TokenApiController {

    private final UserService userService;

    //jwt 로그인
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> jwtLogin(
            @RequestBody JwtTokenLoginRequest request,
            HttpServletResponse response,
            @CookieValue(value = "refreshToken", required = false) Cookie existingRefreshTokenCookie
    ) {
        if (!userService.validateLogin(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(JwtTokenResponse.builder()
                            .message("아이디와 비밀번호를 입력하세요").build());
        }

        // 기존 쿠키 삭제 로직
        if (existingRefreshTokenCookie != null) {
            deleteTokenCookie(response);
        }

        try {
            JwtTokenDto jwtTokenResponse = userService.login(request);

            getCookie(jwtTokenResponse, response);

            return ResponseEntity.ok().body(JwtTokenResponse
                    .builder()
                    .accessToken(jwtTokenResponse.getAccessToken())
                    .refreshToken(jwtTokenResponse.getRefreshToken())
                    .isAdmin(jwtTokenResponse.getRole().equals(UserRole.ADMIN))
                    .message("로그인 성공")
                    .build());
        } catch (UserNotFoundException | UserDeletedException e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(JwtTokenResponse.builder()
                            .message(e.getMessage()).build());
        } catch (PasswordNotMatchException e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JwtTokenResponse.builder()
                            .message(e.getMessage()).build());
        }
    }

//    //토큰 재발급 로직
//    @PostMapping("/refresh-token")
//    public ResponseEntity<RefreshTokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
//        String refreshToken = request.getRefreshToken();
//
//        // refreshToken 검증 및 새로운 accessToken 생성
//        String newAccessToken = tokenService.createNewAccessToken(refreshToken);
//
//        if (newAccessToken == null) {
//            return ResponseEntity.status(401).build(); // 토큰이 유효하지 않은 경우 401 Unauthorized 응답
//        }
//
//        return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
//    }

    //토큰 유효성 api
    @GetMapping("/protected")
    public ResponseEntity<UserValidateTokenResponseDto> getProtectedResource(@CookieValue(
            value = "accessToken", required = false, defaultValue = "") String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(UserValidateTokenResponseDto.builder()
                            .message("not login").build());
        }
        if (userService.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(UserValidateTokenResponseDto.builder()
                            .message("success").build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UserValidateTokenResponseDto.builder()
                            .message("fail").build());
        }
    }

    //엑세스토큰, 리프레시 토큰 쿠키삭제 로직
    private void deleteTokenCookie(HttpServletResponse response) {
        Cookie deleteRefreshTokenCookie = new Cookie(REFRESH_TOKEN_TYPE_VALUE, null);
        deleteRefreshTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
        deleteRefreshTokenCookie.setSecure(true); // HTTPS에서만 전송
        deleteRefreshTokenCookie.setAttribute("SameSite", "Lax");
        deleteRefreshTokenCookie.setPath("/"); // 동일한 경로
        deleteRefreshTokenCookie.setMaxAge(0); // 쿠키 삭제 설정

        Cookie deleteAccessTokenCookie = new Cookie(ACCESS_TOKEN_TYPE_VALUE, null);
        deleteAccessTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
        deleteAccessTokenCookie.setSecure(true); // HTTPS에서만 전송
        deleteAccessTokenCookie.setAttribute("SameSite", "Lax");
        deleteAccessTokenCookie.setPath("/"); // 동일한 경로
        deleteAccessTokenCookie.setMaxAge(0); // 쿠키 삭제 설정

        response.addCookie(deleteRefreshTokenCookie); // 삭제할 쿠키를 response에 추가
        response.addCookie(deleteAccessTokenCookie);
    }

    //쿠키 생성로직
    private void getCookie(JwtTokenDto jwtTokenResponse, HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie(
                REFRESH_TOKEN_TYPE_VALUE,
                jwtTokenResponse.getRefreshToken()
        );

        Cookie accessTokenCookie = new Cookie(
                ACCESS_TOKEN_TYPE_VALUE,
                jwtTokenResponse.getAccessToken()
        );

        refreshTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근할 수 없도록 설정
        refreshTokenCookie.setSecure(true); // HTTPS에서만 전송되도록 설정 (생산 환경에서 사용)
        refreshTokenCookie.setAttribute("SameSite", "Lax"); //보안설정 Lax
        refreshTokenCookie.setPath("/"); // 쿠키의 유효 경로 설정
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키의 유효 기간 설정 (예: 7일)

        accessTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근할 수 없도록 설정
        accessTokenCookie.setSecure(true); // HTTPS에서만 전송되도록 설정 (생산 환경에서 사용)
        accessTokenCookie.setAttribute("SameSite", "Lax"); //보안설정 Lax
        accessTokenCookie.setPath("/"); // 쿠키의 유효 경로 설정
        accessTokenCookie.setMaxAge(15 * 60); // 15분

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokenCookie);
    }
}

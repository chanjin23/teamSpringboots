package com.spring_boots.spring_boots.user.controller;

import com.spring_boots.spring_boots.common.util.CookieUtil;
import com.spring_boots.spring_boots.user.domain.UserRole;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenDto;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenLoginRequest;
import com.spring_boots.spring_boots.user.dto.response.JwtTokenResponse;
import com.spring_boots.spring_boots.user.dto.response.UserCheckAdminResponseDto;
import com.spring_boots.spring_boots.user.dto.response.UserValidateTokenResponseDto;
import com.spring_boots.spring_boots.user.exception.PasswordNotMatchException;
import com.spring_boots.spring_boots.user.exception.UserDeletedException;
import com.spring_boots.spring_boots.user.exception.UserNotFoundException;
import com.spring_boots.spring_boots.user.service.UserAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.spring_boots.spring_boots.config.jwt.JwtConstants.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserAuthApiController {

    private final UserAuthService userAuthService;

    //jwt 로그인
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> jwtLogin(
            @RequestBody JwtTokenLoginRequest request,
            HttpServletResponse response
    ) {
        try {
            JwtTokenDto jwtTokenResponse = userAuthService.login(request);

            CookieUtil.addCookie(response, ACCESS_TOKEN_TYPE_VALUE, jwtTokenResponse.getAccessToken());
            CookieUtil.addCookie(response, REFRESH_TOKEN_TYPE_VALUE, jwtTokenResponse.getRefreshToken());

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
        if (userAuthService.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(UserValidateTokenResponseDto.builder()
                            .message("success").build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UserValidateTokenResponseDto.builder()
                            .message("fail").build());
        }
    }

    //관리자 확인 API
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/admin-check")
    public ResponseEntity<UserCheckAdminResponseDto> checkAdmin(@CookieValue(value = ACCESS_TOKEN_TYPE_VALUE, required = false) String accessToken) {
        //accessToken 이 없는 경우
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UserCheckAdminResponseDto.builder()
                            .message("현재 엑세스 토큰이 없습니다.").build());
        }

        try {
            boolean isAdmin = userAuthService.validateAdminToken(accessToken);

            if (isAdmin) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(UserCheckAdminResponseDto.builder().message("관리자 인증 성공").build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(UserCheckAdminResponseDto.builder().message("관리자 인증 실패").build());
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UserCheckAdminResponseDto.builder().message("유효하지않은 토큰").build());
        }
    }
}

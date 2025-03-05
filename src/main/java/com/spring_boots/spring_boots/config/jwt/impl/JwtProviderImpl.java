package com.spring_boots.spring_boots.config.jwt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_boots.spring_boots.user.domain.Provider;
import com.spring_boots.spring_boots.user.domain.UserRole;
import com.spring_boots.spring_boots.user.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.spring_boots.spring_boots.config.jwt.JwtConstants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProviderImpl {
    @Value("${jwt.secret}") //설정 정보 파일에 값을 가져옴.
    private String secret;

    @Value("${jwt.token.access-expires}")
    private long accessExpires;

    @Value("${jwt.token.refresh-expires}")
    private long refreshExpires;

    private Key key;

    private final UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Authentication getAuthentication(String authToken) {
        String username = extractUsername(authToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public AuthTokenImpl createAuthToken(
            String userRealId,  //실제 아이디
            String role,
            Long userId,    //pk 아이디
            String provider,
            String tokenType
    ) {
        long expiredDate = createExpiredDate(tokenType);

        Claims claims = createClaims(
                userRealId, role, userId, provider, tokenType
        );

        return new AuthTokenImpl(userRealId, key, claims, new Date(expiredDate));
    }

    private long createExpiredDate(String tokenType) {
        long time = System.currentTimeMillis();

        if (tokenType.equals(ACCESS_TOKEN_TYPE_VALUE)) time += accessExpires;
        else time += refreshExpires;

        return time;
    }

    private Claims createClaims(String userRealId, String role,
                                Long userId, String provider,
                                String tokenType) {
        Map<String, Object> claims = Map.of(
                "accountId", userId,
                "provider", provider,
                "role", role,
                "type", tokenType,
                "userRealId", userRealId
        );
        return new DefaultClaims(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //토큰 정보를 비밀키를 이용해 해석
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // JWT가 만료된 경우에도 Claims를 반환할 수 있도록 예외에서 Claims를 가져옴
            return e.getClaims();  // 만료된 토큰에서 Claims 정보를 가져옴
        }
    }


    //토큰 만료여부에 따라 true or false 반환
    public boolean validateToken(String jwtToken) {
        return !isTokenExpired(jwtToken);
    }

    //토큰 만료여부
    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    //날짜 추출
    private Date extractExpiration(String jwtToken) {
        return extractAllClaims(jwtToken).getExpiration();
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰 생성
    public TokenDto generateAccessTokenAndRefreshToken(String refreshToken) throws JsonProcessingException {
        // 리프레시 토큰 검증
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 리프레시 토큰에서 사용자 정보를 추출
        Claims claims = extractAllClaims(refreshToken); //리프레시토큰에 있는 모든 정보를 추출
        String userRealId = claims.get("userRealId", String.class); // 사용자 실제 ID
        Long userId = claims.get("accountId", Long.class); // 계정 ID
        String role = claims.get("role", String.class); // 사용자 역할
        String provider = claims.get("provider", String.class);

        AuthTokenImpl newAccessToken = createAuthToken(userRealId, role, userId, provider, ACCESS_TOKEN_TYPE_VALUE);
        AuthTokenImpl newRefreshToken = createAuthToken(userRealId, role, userId, provider, REFRESH_TOKEN_TYPE_VALUE);

        return TokenDto.builder()
                .accessToken(newAccessToken.getToken())
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    public boolean validateAdminToken(String accessToken) {
        try {
            // JWT에서 Claims(토큰에 포함된 정보) 파싱
            Claims claims = extractAllClaims(accessToken);

            // Claims에서 사용자 역할 정보 추출 (예: "ROLE_ADMIN"인지 확인)
            String role = claims.get("role", String.class);

            // 사용자 역할이 관리자("ROLE_ADMIN")이면 true 반환
            return "ADMIN".equals(role);
        } catch (SignatureException e) {
            // 서명이 유효하지 않은 경우 (토큰이 변조되었을 때)
            throw new RuntimeException("Invalid JWT signature");
        } catch (Exception e) {
            // 그 외의 오류 (만료된 토큰 등)
            throw new RuntimeException("Token validation error: " + e.getMessage());
        }
    }
}

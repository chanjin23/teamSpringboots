package com.spring_boots.spring_boots.user.service;

import com.spring_boots.spring_boots.config.jwt.impl.AuthTokenImpl;
import com.spring_boots.spring_boots.config.jwt.impl.JwtProviderImpl;
import com.spring_boots.spring_boots.user.domain.Users;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenDto;
import com.spring_boots.spring_boots.user.dto.request.JwtTokenLoginRequest;
import com.spring_boots.spring_boots.user.exception.PasswordNotMatchException;
import com.spring_boots.spring_boots.user.exception.UserDeletedException;
import com.spring_boots.spring_boots.user.exception.UserNotFoundException;
import com.spring_boots.spring_boots.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.spring_boots.spring_boots.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;
import static com.spring_boots.spring_boots.config.jwt.UserConstants.REFRESH_TOKEN_TYPE_VALUE;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final JwtProviderImpl jwtProvider;

    public JwtTokenDto login(JwtTokenLoginRequest request) {
        Users user = validateUser(request);

        AuthTokenImpl accessToken = getAuthToken(user, ACCESS_TOKEN_TYPE_VALUE);
        AuthTokenImpl refreshToken = getAuthToken(user, REFRESH_TOKEN_TYPE_VALUE);

        return JwtTokenDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .role(user.getRole())
                .build();
    }

    private AuthTokenImpl getAuthToken(Users user, String type) {
        return jwtProvider.createAuthToken(
                user.getUserRealId(),   //토큰에 실제 ID 정보 입력
                user.getRole(),
                user.getUserId(),
                user.getProvider(),
                type
        );
    }

    private Users validateUser(JwtTokenLoginRequest request) {
        Users user = userRepository.findByUserRealId(request.getUserRealId())
                .orElseThrow(() -> new UserNotFoundException("가입되지 않은 ID 입니다."));

        if (user.isDeleted()) {
            throw new UserDeletedException("회원 정보가 삭제된 상태입니다.");
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("잘못된 비밀번호입니다.");
        }
        return user;
    }
}

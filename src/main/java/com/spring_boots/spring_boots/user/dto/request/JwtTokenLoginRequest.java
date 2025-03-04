package com.spring_boots.spring_boots.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenLoginRequest {
    @NotBlank(message = "아이디를 입력하지 않았습니다.")
    private String userRealId;
    @NotBlank(message = "비밀번호는 입력하지 않았습니다.")
    private String password;
}

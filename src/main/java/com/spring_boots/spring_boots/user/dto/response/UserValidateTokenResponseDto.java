package com.spring_boots.spring_boots.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserValidateTokenResponseDto {
    private String message;
}

package com.spring_boots.spring_boots.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCheckIdResponseDto {
    private boolean isAvailable;
    private String message;
}

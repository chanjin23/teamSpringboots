package com.spring_boots.spring_boots.user.dto.response;

import com.spring_boots.spring_boots.user.domain.Provider;
import com.spring_boots.spring_boots.user.domain.UserRole;
import com.spring_boots.spring_boots.user.domain.UsersInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminUserResponseDto {
    private Long userId;
    private String username;
    private String userRealId;
    private String email;
    private UserRole role;
    private Provider provider;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    public UserResponseDto toResponseDto() {
        return UserResponseDto.builder()
                .userId(userId)
                .email(email)
                .username(username)
                .userRealId(userRealId)
                .provider(provider)
                .role(role)
                .isDeleted(isDeleted)
                .createdAt(getCreatedAt())
                .message("사용자 있음")
                .build();
    }
}

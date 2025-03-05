package com.spring_boots.spring_boots.user.service;

import com.spring_boots.spring_boots.user.domain.Users;
import com.spring_boots.spring_boots.user.dto.request.AdminCodeRequestDto;
import com.spring_boots.spring_boots.user.dto.request.AdminGrantTokenRequestDto;
import com.spring_boots.spring_boots.user.dto.response.AdminUserResponseDto;
import com.spring_boots.spring_boots.user.dto.response.UserAdminCountResponseDto;
import com.spring_boots.spring_boots.user.dto.response.UserResponseDto;
import com.spring_boots.spring_boots.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${admin.code}")
    private String adminCode;

    public Page<UserResponseDto> getUsersByCreatedAt(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponseDto> usersPage=userRepository.findUsers(pageable);

        return usersPage.map(AdminUserResponseDto::toResponseDto);
    }

    public UserAdminCountResponseDto countUsers() {
        long countAdmin=userRepository.countAdmin();
        long totalUser = userRepository.count();
        return UserAdminCountResponseDto.builder()
                .countAdmin(countAdmin)
                .totalUser(totalUser)
                .build();
    }

    @Transactional
    public void grantRole(Users authUser, AdminGrantTokenRequestDto adminGrantTokenRequestDto) {
        authUser.updateToRole(adminGrantTokenRequestDto);
    }

    //관리자코드체크
    public boolean checkAdminCode(AdminCodeRequestDto adminCodeDto) {
        //임의 토큰 만들기
        String tempAdminCode = bCryptPasswordEncoder.encode(adminCode);
        String adminCode = adminCodeDto.getAdminCode();
        if (bCryptPasswordEncoder.matches(adminCode, tempAdminCode)) {
            return true;
        } else {
            log.info("잘못된 관리자 토큰");
            return false;
        }
    }
}

package com.spring_boots.spring_boots.user.controller;

import com.spring_boots.spring_boots.user.domain.Users;
import com.spring_boots.spring_boots.user.dto.UserDto;
import com.spring_boots.spring_boots.user.dto.request.AdminCodeRequestDto;
import com.spring_boots.spring_boots.user.dto.request.AdminGrantTokenRequestDto;
import com.spring_boots.spring_boots.user.dto.response.*;
import com.spring_boots.spring_boots.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserAdminApiController {

    private final UserService userService;

    //모든 회원 정보 조회(관리자)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserResponseDto> result = userService.getUsersByCreatedAt(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/count")
    public ResponseEntity<UserAdminCountResponseDto> countUsers() {
        UserAdminCountResponseDto userAdminCountResponseDto = userService.countUsers();
        return ResponseEntity.status(HttpStatus.OK).body(userAdminCountResponseDto);
    }

    //관리자 본인인지아닌지
    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("/admin/user/principal")
    public ResponseEntity<UserPrincipalAdminResponseDto> checkPrincipalAdmin(UserDto userDto) {
        Long userId = userDto.getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(
                UserPrincipalAdminResponseDto.builder().userId(userId).build());
    }

    //특정 회원 정보 조회(관리자)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/{user_id}")
    public ResponseEntity<UserResponseDto> getUserByAdmin(@PathVariable("user_id") Long userId) {
        Users findUser = userService.findById(userId);
        UserResponseDto responseDto = findUser.toResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //    //관리자 부여
    @PatchMapping("/admin/grant/{userId}")
    public ResponseEntity<AdminGrantTokenResponseDto> grantAdmin(@PathVariable("userId") Long userId,
                                                                 @RequestBody AdminGrantTokenRequestDto adminGrantTokenRequestDto) {
        Users authUser = userService.findById(userId);

        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AdminGrantTokenResponseDto.builder()
                            .message("인증되지 않은 사용자입니다. 로그인해주세요").build());
        }
        userService.grantRole(authUser, adminGrantTokenRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(AdminGrantTokenResponseDto.builder()
                .message("권한부여 성공!").build());
    }

    //관리자 코드 체크 API
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/grant")
    public ResponseEntity<AdminCodeResponseDto> checkAdminCode(@RequestBody AdminCodeRequestDto adminCodeDto) {
        if (userService.checkAdminCode(adminCodeDto)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(AdminCodeResponseDto.builder().message("success").build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AdminCodeResponseDto.builder().message("fail").build());
        }
    }
}

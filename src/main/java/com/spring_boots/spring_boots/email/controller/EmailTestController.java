package com.spring_boots.spring_boots.email.controller;

import com.spring_boots.spring_boots.email.dto.EmailMessage;
import com.spring_boots.spring_boots.email.service.EmailService;
import com.spring_boots.spring_boots.user.domain.Users;
import com.spring_boots.spring_boots.user.dto.UserDto;
import com.spring_boots.spring_boots.user.repository.UserRepository;
import com.spring_boots.spring_boots.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EmailTestController {
    private final EmailService emailService;
    private final UserService userService;

    @PostMapping("/test-email")
    public ResponseEntity<?> testSendEmail(UserDto userDto) {
        Users findUser = userService.findById(userDto.getUserId());

        emailService.sendEmail(findUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

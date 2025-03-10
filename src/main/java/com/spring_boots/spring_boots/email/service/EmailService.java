package com.spring_boots.spring_boots.email.service;

import com.spring_boots.spring_boots.email.dto.EmailMessage;
import com.spring_boots.spring_boots.user.domain.Users;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            mimeMessageHelper.setText(emailMessage.getMessage()); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

            log.info("이메일 Success");
        } catch (MessagingException e) {
            log.error("이메일 fail");
            throw new RuntimeException(e);
        }
    }

    public EmailMessage createEmail(Users findUser) {
        return EmailMessage.builder()
                .to(findUser.getEmail())
                .subject("테스트입니다.")
                .message("테스트입니다.")
                .build();
    }
}

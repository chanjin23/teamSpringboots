package com.spring_boots.spring_boots.email.service;

import com.spring_boots.spring_boots.email.dto.EmailMessage;
import com.spring_boots.spring_boots.orders.dto.OrderRequestDto;
import com.spring_boots.spring_boots.user.domain.Users;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

//    @Async("threadPoolTaskExecutor")
    public void sendEmail(Users user) {
        EmailMessage email = createEmail(user);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(email.getSubject()); // 메일 제목
            mimeMessageHelper.setText(email.getMessage()); // 메일 본문 내용, HTML 여부
            mailSender.send(mimeMessage);

            log.info("이메일 Success");
        } catch (MessagingException e) {
            log.error("이메일 fail");
            throw new RuntimeException(e);
        }
    }

    public EmailMessage createEmail(Users findUser) {
        return EmailMessage.builder()
                .to(findUser.getEmail())
                .subject("")
                .message("테스트입니다.")
                .build();
    }

    @Async("threadPoolTaskExecutor")
    public void sendEmailByOrder(Users user, OrderRequestDto request) {
        String subject = String.format("[주문 확인] %s님, 주문이 정상적으로 접수되었습니다!", request.getRecipientName());
        String content = buildEmailContent(request);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("이메일 전송 완료");
        } catch (MessagingException e) {
            log.error("이메일 전송 실패");
            throw new RuntimeException("이메일 전송 중 오류 발생", e);
        }
    }

    private String buildEmailContent(OrderRequestDto request) {
        StringBuilder content = new StringBuilder();
        content.append(String.format("<p>안녕하세요, %s님.</p>", request.getRecipientName()));
        content.append("<p>고객님의 주문이 정상적으로 접수되었습니다. 아래의 주문 정보를 확인해주세요.</p>");
        content.append("<hr>");

        // 주문 정보
        content.append("<h3>📦 주문 정보</h3>");
        content.append(String.format("<p><strong>주문자명:</strong> %s</p>", request.getRecipientName()));
        content.append(String.format("<p><strong>배송지:</strong> %s</p>", request.getShippingAddress()));
        content.append(String.format("<p><strong>수령인 연락처:</strong> %s</p>", request.getRecipientContact()));
        content.append(String.format("<p><strong>배송 요청 사항:</strong> %s</p>", request.getDeliveryMessage()));

        // 주문 상품 리스트
        content.append("<h3>🛍 주문 상품</h3>");
        content.append("<table border='1' cellpadding='5' cellspacing='0'><tr><th>상품 ID</th><th>수량</th><th>사이즈</th><th>가격</th></tr>");
        int totalPrice = 0;
        for (OrderRequestDto.OrderItemDto item : request.getItems()) {
            content.append(String.format(
                    "<tr><td>%d</td><td>%d개</td><td>%d</td><td>%d원</td></tr>",
                    item.getItemId(), item.getItemQuantity(), item.getItemSize(), item.getItemPrice()
            ));
            totalPrice += item.getItemPrice() * item.getItemQuantity();
        }
        content.append("</table>");

        // 총 결제 금액
        content.append(String.format("<h3>💰 총 결제 금액: %d원</h3>", totalPrice));
        content.append("<hr>");

        // 안내 문구
        content.append("<p>📢 <strong>추가 안내</strong></p>");
//        content.append("<p>배송이 시작되면 별도의 안내 이메일을 보내드리겠습니다.</p>");
        content.append("<p>문의사항이 있으시면 언제든지 고객센터로 연락해주세요.</p>");
        content.append("<p>감사합니다. 😊</p>");
        content.append("<p><strong>[Spring-Boots] 드림</strong></p>");

        return content.toString();
    }
}

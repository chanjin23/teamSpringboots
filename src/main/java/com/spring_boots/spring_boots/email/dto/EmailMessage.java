package com.spring_boots.spring_boots.email.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailMessage {
    private String to;
    private String subject;
    private String message;
}

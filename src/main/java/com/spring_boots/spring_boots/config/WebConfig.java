package com.spring_boots.spring_boots.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * url 패스 경로 바꾸기
 * addViewController("원하는 경로").setViewName("forward:현재 정적 경로")
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /login 으로 요청이 들어오면 login/login.html로 매핑
        registry.addViewController("/login").setViewName("forward:/login/login.html");
        //forward 는 서버 내에서 요청을 리다이렉트하지 않고 다른 경로로 넘겨주는 방식, 내부적으로 처리됨.
        registry.addViewController("/order-summary").setViewName("forward:/order-summary/order-summary.html");
    }
}
package com.example.lovekeeper.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        // 테스트용 JavaMailSender 구현
        return new JavaMailSenderImpl();
    }
    
    // 필요한 경우 다른 테스트용 빈 구성 추가
}

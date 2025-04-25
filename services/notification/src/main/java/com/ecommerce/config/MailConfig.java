package com.ecommerce.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MailConfig {

    @Value("${mail.sender.host}")
    private String host;

    @Value("${mail.sender.port}")
    private int port;

    @Value("${mail.sender.username}")
    private String username;

    @Value("${mail.sender.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(mailProperties());

        log.info("JavaMailSender initialized with host={} and port={}", host, port);
        return mailSender;
    }

    private Properties mailProperties() {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "false");

        return properties;
    }
}

package com.ecommerce.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MailConfig.class)
@TestPropertySource(properties = {
        "mail.sender.host=test-smtp.example.com",
        "mail.sender.port=587",
        "mail.sender.username=test-user",
        "mail.sender.password=test-password"
})
class MailConfigTest {

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    void javaMailSenderShouldBeInitializedCorrectly() {
        // Assert that JavaMailSender is initialized
        assertNotNull(javaMailSender);

        // Cast to implementation class to check specific properties
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) javaMailSender;

        // Verify the mail sender is configured with the expected properties
        assertEquals("test-smtp.example.com", mailSender.getHost());
        assertEquals(587, mailSender.getPort());
        assertEquals("test-user", mailSender.getUsername());
        assertEquals("test-password", mailSender.getPassword());
    }

    @Test
    void mailPropertiesShouldBeConfiguredCorrectly() {
        // Cast to implementation class to check properties
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) javaMailSender;
        Properties props = mailSender.getJavaMailProperties();

        // Verify SMTP properties are set correctly
        assertEquals("smtp", props.getProperty("mail.transport.protocol"));
        assertEquals("true", props.getProperty("mail.smtp.auth"));
        assertEquals("true", props.getProperty("mail.smtp.starttls.enable"));
        assertEquals("false", props.getProperty("mail.debug"));
    }
}
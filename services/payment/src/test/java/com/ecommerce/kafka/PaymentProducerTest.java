package com.ecommerce.kafka;

import com.ecommerce.feignclient.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentProducerTest {

    @Mock
    private KafkaTemplate<String, PaymentConfirmation> kafkaTemplate;

    @Captor
    private ArgumentCaptor<Message<PaymentConfirmation>> messageCaptor;

    private PaymentProducer paymentProducer;
    private PaymentConfirmation paymentConfirmation;

    @BeforeEach
    void setUp() {
        paymentProducer = new PaymentProducer(kafkaTemplate);

        // Create a sample PaymentConfirmation for testing
        UserResponse userResponse = new UserResponse(
                1L, "First name", "Second name", "test@example.com"
        );

        paymentConfirmation = PaymentConfirmation.builder()
                .orderId(1001L)
                .totalPrice(new BigDecimal("99.99"))
                .paymentMethodName("Credit Card")
                .user(userResponse)
                .sessionUri("https://payment.example.com/session/123")
                .OrderCreateTime("2025-04-07T12:00:00")
                .build();
    }

    @Test
    void sendPaymentConfirmation_ShouldSendMessageToCorrectTopic() {
        // Act
        paymentProducer.sendPaymentConfirmation(paymentConfirmation);

        // Assert
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<PaymentConfirmation> capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertEquals(paymentConfirmation, capturedMessage.getPayload());
        assertEquals("payment-created-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
    }

    @Test
    void sendPaymentSuccessful_ShouldSendMessageToCorrectTopic() {
        // Act
        paymentProducer.sendPaymentSuccessful(paymentConfirmation);

        // Assert
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<PaymentConfirmation> capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertEquals(paymentConfirmation, capturedMessage.getPayload());
        assertEquals("payment-success-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
    }
}
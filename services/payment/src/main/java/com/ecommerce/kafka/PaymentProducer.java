package com.ecommerce.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
    private final KafkaTemplate<String, PaymentConfirmation> kafkaTemplate;

    public void sendPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
        Message<PaymentConfirmation> message = MessageBuilder
                .withPayload(paymentConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "payment-created-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendPaymentSuccessful(PaymentConfirmation paymentConfirmation) {
        Message<PaymentConfirmation> message = MessageBuilder
                .withPayload(paymentConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "payment-success-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
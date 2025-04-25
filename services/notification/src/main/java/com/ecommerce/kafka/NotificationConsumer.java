package com.ecommerce.kafka;

import com.ecommerce.email.EmailService;
import com.ecommerce.kafka.order.OrderConfirmation;
import com.ecommerce.kafka.payment.PaymentConfirmation;
import com.ecommerce.notification.Notification;
import com.ecommerce.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.ecommerce.notification.NotificationType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    /**
     * Listens for order confirmation messages and processes them.
     *
     * @param orderConfirmation Received order confirmation event.
     */
    @KafkaListener(topics = "order-confirmation-topic", groupId = "notification-group")
    public void consumeOrderConfirmationNotification(OrderConfirmation orderConfirmation) {
        Objects.requireNonNull(orderConfirmation, "OrderConfirmation nesmí být prázdné.");
        log.debug("Received order confirmation event for orderId: {}", orderConfirmation.orderId());

        try {
            notificationRepository.save(
                    Notification.builder()
                            .type(ORDER_CONFIRMATION)
                            .date(LocalDateTime.now())
                            .orderConfirmation(orderConfirmation)
                            .build()
            );

            emailService.sendOrderConfirmationEmail(orderConfirmation);
            log.info("Order confirmation notification processed for orderId: {}", orderConfirmation.orderId());
        } catch (Exception e) {
            log.error("Failed to process order confirmation notification for orderId: {}", orderConfirmation.orderId(), e);
        }
    }

    /**
     * Listens for payment creation messages and processes them.
     *
     * @param paymentConfirmation Received payment creation event.
     */
    @KafkaListener(topics = "payment-created-topic", groupId = "notification-group")
    public void consumePaymentCreationNotification(PaymentConfirmation paymentConfirmation) {
        Objects.requireNonNull(paymentConfirmation, "PaymentConfirmation nesmí být prázdné.");
        log.debug("Received payment creation event for orderId: {}", paymentConfirmation.orderId());

        try {
            notificationRepository.save(
                    Notification.builder()
                            .type(PAYMENT_CREATED)
                            .date(LocalDateTime.now())
                            .paymentConfirmation(paymentConfirmation)
                            .build()
            );

            emailService.sendPaymentCreationEmail(paymentConfirmation);
            log.info("Payment creation notification processed for orderId: {}", paymentConfirmation.orderId());
        } catch (Exception e) {
            log.error("Failed to process payment creation notification for orderId: {}", paymentConfirmation.orderId(), e);
        }
    }

    /**
     * Listens for payment success messages and processes them.
     *
     * @param paymentConfirmation Received payment success event.
     */
    @KafkaListener(topics = "payment-success-topic", groupId = "notification-group")
    public void consumePaymentSuccessfulNotification(PaymentConfirmation paymentConfirmation) {
        Objects.requireNonNull(paymentConfirmation, "PaymentConfirmation nesmí být prázdné.");
        log.debug("Received payment success event for orderId: {}", paymentConfirmation.orderId());

        try {
            notificationRepository.save(
                    Notification.builder()
                            .type(PAYMENT_SUCCESSFUL)
                            .date(LocalDateTime.now())
                            .paymentConfirmation(paymentConfirmation)
                            .build()
            );

            emailService.sendPaymentSuccessfulEmail(paymentConfirmation);
            log.info("Payment successful notification processed for orderId: {}", paymentConfirmation.orderId());
        } catch (Exception e) {
            log.error("Failed to process payment successful notification for orderId: {}", paymentConfirmation.orderId(), e);
        }
    }
}

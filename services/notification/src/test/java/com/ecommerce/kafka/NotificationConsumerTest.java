package com.ecommerce.kafka;

import com.ecommerce.email.EmailService;
import com.ecommerce.kafka.order.OrderConfirmation;
import com.ecommerce.kafka.payment.PaymentConfirmation;
import com.ecommerce.notification.Notification;
import com.ecommerce.notification.NotificationRepository;
import com.ecommerce.notification.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Mock
    private OrderConfirmation orderConfirmation;

    @Mock
    private PaymentConfirmation paymentConfirmation;

    @Test
    void consumeOrderConfirmationNotificationShouldSaveAndSendEmail() {
        // Given
        when(orderConfirmation.orderId()).thenReturn(123L);

        // When
        notificationConsumer.consumeOrderConfirmationNotification(orderConfirmation);

        // Then
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals(NotificationType.ORDER_CONFIRMATION, savedNotification.getType());
        assertEquals(orderConfirmation, savedNotification.getOrderConfirmation());
        assertNotNull(savedNotification.getDate());

        verify(emailService).sendOrderConfirmationEmail(orderConfirmation);
    }

    @Test
    void consumePaymentCreationNotificationShouldSaveAndSendEmail() {
        // Given
        when(paymentConfirmation.orderId()).thenReturn(456L);

        // When
        notificationConsumer.consumePaymentCreationNotification(paymentConfirmation);

        // Then
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals(NotificationType.PAYMENT_CREATED, savedNotification.getType());
        assertEquals(paymentConfirmation, savedNotification.getPaymentConfirmation());
        assertNotNull(savedNotification.getDate());

        verify(emailService).sendPaymentCreationEmail(paymentConfirmation);
    }

    @Test
    void consumePaymentSuccessfulNotificationShouldSaveAndSendEmail() {
        // Given
        when(paymentConfirmation.orderId()).thenReturn(456L);

        // When
        notificationConsumer.consumePaymentSuccessfulNotification(paymentConfirmation);

        // Then
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals(NotificationType.PAYMENT_SUCCESSFUL, savedNotification.getType());
        assertEquals(paymentConfirmation, savedNotification.getPaymentConfirmation());
        assertNotNull(savedNotification.getDate());

        verify(emailService).sendPaymentSuccessfulEmail(paymentConfirmation);
    }

    @Test
    void shouldThrowExceptionWhenOrderConfirmationIsNull() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                notificationConsumer.consumeOrderConfirmationNotification(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPaymentConfirmationIsNull() {
        // When / Then
        assertThrows(NullPointerException.class, () ->
                notificationConsumer.consumePaymentCreationNotification(null)
        );

        assertThrows(NullPointerException.class, () ->
                notificationConsumer.consumePaymentSuccessfulNotification(null)
        );
    }

    @Test
    void shouldHandleExceptionWhenRepositorySaveFails() {
        // Given
        when(orderConfirmation.orderId()).thenReturn(123L);
        when(notificationRepository.save(any(Notification.class))).thenThrow(new RuntimeException("Database error"));

        // When - this should not throw despite the repository error
        notificationConsumer.consumeOrderConfirmationNotification(orderConfirmation);

        // Then - verify email service was never called due to the exception
        verify(emailService, times(0)).sendOrderConfirmationEmail(any());
    }

    @Test
    void shouldHandleExceptionWhenEmailServiceFails() {
        // Given
        when(paymentConfirmation.orderId()).thenReturn(456L);
        doThrow(new RuntimeException("Email sending failed")).when(emailService).sendPaymentCreationEmail(any());

        // When - this should not throw despite the email service error
        notificationConsumer.consumePaymentCreationNotification(paymentConfirmation);

        // Then - verify the notification was still saved
        verify(notificationRepository).save(any(Notification.class));
    }
}
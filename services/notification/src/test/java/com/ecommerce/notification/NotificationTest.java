package com.ecommerce.notification;

import com.ecommerce.kafka.order.OrderConfirmation;
import com.ecommerce.kafka.payment.PaymentConfirmation;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void shouldCreateNotificationWithBuilder() {
        // Given
        String id = "123";
        NotificationType type = NotificationType.ORDER_CONFIRMATION;
        LocalDateTime date = LocalDateTime.now();
        OrderConfirmation orderConfirmation = createDummyOrderConfirmation();

        // When
        Notification notification = Notification.builder()
                .id(id)
                .type(type)
                .date(date)
                .orderConfirmation(orderConfirmation)
                .build();

        // Then
        assertEquals(id, notification.getId());
        assertEquals(type, notification.getType());
        assertEquals(date, notification.getDate());
        assertEquals(orderConfirmation, notification.getOrderConfirmation());
        assertNull(notification.getPaymentConfirmation());
    }

    @Test
    void shouldCreateNotificationWithPaymentConfirmation() {
        // Given
        NotificationType type = NotificationType.PAYMENT_SUCCESSFUL;
        LocalDateTime date = LocalDateTime.now();
        PaymentConfirmation paymentConfirmation = createDummyPaymentConfirmation();

        // When
        Notification notification = Notification.builder()
                .type(type)
                .date(date)
                .paymentConfirmation(paymentConfirmation)
                .build();

        // Then
        assertNull(notification.getId()); // ID should be null as not set
        assertEquals(type, notification.getType());
        assertEquals(date, notification.getDate());
        assertEquals(paymentConfirmation, notification.getPaymentConfirmation());
        assertNull(notification.getOrderConfirmation());
    }

    @Test
    void shouldAllowSettingValuesViaSetters() {
        // Given
        Notification notification = new Notification();
        String id = "456";
        NotificationType type = NotificationType.PAYMENT_CREATED;
        LocalDateTime date = LocalDateTime.now();
        PaymentConfirmation paymentConfirmation = createDummyPaymentConfirmation();

        // When
        notification.setId(id);
        notification.setType(type);
        notification.setDate(date);
        notification.setPaymentConfirmation(paymentConfirmation);

        // Then
        assertEquals(id, notification.getId());
        assertEquals(type, notification.getType());
        assertEquals(date, notification.getDate());
        assertEquals(paymentConfirmation, notification.getPaymentConfirmation());
    }

    @Test
    void shouldCorrectlyImplementEquals() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        OrderConfirmation orderConfirmation = createDummyOrderConfirmation();

        Notification notification1 = Notification.builder()
                .id("123")
                .type(NotificationType.ORDER_CONFIRMATION)
                .date(now)
                .orderConfirmation(orderConfirmation)
                .build();

        Notification notification2 = Notification.builder()
                .id("123")
                .type(NotificationType.ORDER_CONFIRMATION)
                .date(now)
                .orderConfirmation(orderConfirmation)
                .build();

        Notification notification3 = Notification.builder()
                .id("456")
                .type(NotificationType.ORDER_CONFIRMATION)
                .date(now)
                .orderConfirmation(orderConfirmation)
                .build();

        // Then
        assertEquals(notification1, notification2);
        assertEquals(notification1.hashCode(), notification2.hashCode());

        // Different objects should not be equal
        assertNotEquals(notification1, notification3);
    }

    @Test
    void shouldCorrectlyImplementToString() {
        // Given
        Notification notification = Notification.builder()
                .id("123")
                .type(NotificationType.ORDER_CONFIRMATION)
                .date(LocalDateTime.of(2025, 4, 6, 10, 30))
                .orderConfirmation(createDummyOrderConfirmation())
                .build();

        // When
        String toString = notification.toString();

        // Then
        assertNotNull(toString);
        // Verify the toString contains basic fields
        assertTrue(toString.contains("id=123"));
        assertTrue(toString.contains("type=ORDER_CONFIRMATION"));
        assertTrue(toString.contains("date=2025-04-06T10:30"));
        assertTrue(toString.contains("orderConfirmation="));
    }

    // Helper methods to create test data

    private OrderConfirmation createDummyOrderConfirmation() {
        // Since OrderConfirmation class is not provided, we would normally create a mock or stub
        // For now, return null or create a mock if needed in a real test
        return null; // Replace with actual implementation once OrderConfirmation is defined
    }

    private PaymentConfirmation createDummyPaymentConfirmation() {
        // Since PaymentConfirmation class is not provided, we would normally create a mock or stub
        // For now, return null or create a mock if needed in a real test
        return null; // Replace with actual implementation once PaymentConfirmation is defined
    }

    // Add this method to test toString since we can't see the actual Lombok implementation
    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
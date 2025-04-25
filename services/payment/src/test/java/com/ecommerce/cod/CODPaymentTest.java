package com.ecommerce.cod;

import com.ecommerce.payment.PaymentStatus;
import com.ecommerce.paymentmethod.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CODPaymentTest {

    @Test
    void testBuilderAndGetters() {
        // Arrange
        Long id = 1L;
        Long orderId = 1001L;
        BigDecimal totalPrice = new BigDecimal("150.00");
        PaymentStatus status = PaymentStatus.CASH_ON_DELIVERY;
        PaymentMethod method = new PaymentMethod();
        LocalDateTime createTime = LocalDateTime.now();

        // Act
        CODPayment payment = CODPayment.builder().build();

        payment.setId(id);
        payment.setOrderId(orderId);
        payment.setTotalPrice(totalPrice);
        payment.setMethod(method);
        payment.setStatus(status);
        payment.setCreateTime(createTime);

        // Assert
        assertEquals(id, payment.getId(), "ID should match");
        assertEquals(orderId, payment.getOrderId(), "Order ID should match");
        assertEquals(totalPrice, payment.getTotalPrice(), "Total price should match");
        assertEquals(status, payment.getStatus(), "Status should match");
        assertEquals(method, payment.getMethod(), "Payment method should match");
        assertEquals(createTime, payment.getCreateTime(), "Create time should match");
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        CODPayment payment1 = CODPayment.builder().build();
        payment1.setId(1L);
        CODPayment payment2 = CODPayment.builder().build();
        payment2.setId(2L);
        CODPayment payment3 = CODPayment.builder().build();
        payment3.setId(3L);

        // Assert
        assertEquals(payment1, payment1, "Payments with the same ID should be equal");
        assertNotEquals(payment1, payment3, "Payments with different IDs should not be equal");
        assertEquals(payment2.hashCode(), payment2.hashCode(), "Hash codes should be equal for equal payments");
        assertNotEquals(payment1.hashCode(), payment3.hashCode(), "Hash codes should differ for different payments");
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        CODPayment payment = new CODPayment();

        // Assert
        assertNull(payment.getId(), "ID should be null for a new payment");
        assertNull(payment.getOrderId(), "Order ID should be null");
        assertNull(payment.getTotalPrice(), "Total price should be null");
        assertNull(payment.getStatus(), "Status should be null");
        assertNull(payment.getMethod(), "Method should be null");
    }

    @Test
    void testSetterMethods() {
        // Arrange
        CODPayment payment = new CODPayment();
        Long id = 1L;
        Long orderId = 1001L;
        BigDecimal totalPrice = new BigDecimal("150.00");
        PaymentStatus status = PaymentStatus.CASH_ON_DELIVERY;
        PaymentMethod method = new PaymentMethod();
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateDate = LocalDateTime.now().plusDays(1);

        // Act
        payment.setId(id);
        payment.setOrderId(orderId);
        payment.setTotalPrice(totalPrice);
        payment.setStatus(status);
        payment.setMethod(method);
        payment.setCreateTime(createTime);
        payment.setUpdateDate(updateDate);

        // Assert
        assertEquals(id, payment.getId(), "ID should match");
        assertEquals(orderId, payment.getOrderId(), "Order ID should match");
        assertEquals(totalPrice, payment.getTotalPrice(), "Total price should match");
        assertEquals(status, payment.getStatus(), "Status should match");
        assertEquals(method, payment.getMethod(), "Payment method should match");
        assertEquals(createTime, payment.getCreateTime(), "Create time should match");
        assertEquals(updateDate, payment.getUpdateDate(), "Update date should match");
    }
}
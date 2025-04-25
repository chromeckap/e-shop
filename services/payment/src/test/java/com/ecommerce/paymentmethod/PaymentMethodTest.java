package com.ecommerce.paymentmethod;

import com.ecommerce.payment.Payment;
import com.ecommerce.strategy.PaymentGatewayType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    @Test
    void testBuilderAndGetters() {
        // Arrange
        Long id = 1L;
        String name = "Credit Card";
        PaymentGatewayType gatewayType = PaymentGatewayType.STRIPE_CARD;
        boolean isActive = true;
        BigDecimal price = new BigDecimal("5.99");
        boolean isFreeForOrderAbove = true;
        BigDecimal freeForOrderAbove = new BigDecimal("100.00");
        Set<Payment> payments = new HashSet<>();

        // Act
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id(id)
                .name(name)
                .gatewayType(gatewayType)
                .isActive(isActive)
                .price(price)
                .isFreeForOrderAbove(isFreeForOrderAbove)
                .freeForOrderAbove(freeForOrderAbove)
                .payments(payments)
                .build();

        // Assert
        assertEquals(id, paymentMethod.getId());
        assertEquals(name, paymentMethod.getName());
        assertEquals(gatewayType, paymentMethod.getGatewayType());
        assertEquals(isActive, paymentMethod.isActive());
        assertEquals(price, paymentMethod.getPrice());
        assertEquals(isFreeForOrderAbove, paymentMethod.isFreeForOrderAbove());
        assertEquals(freeForOrderAbove, paymentMethod.getFreeForOrderAbove());
        assertEquals(payments, paymentMethod.getPayments());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        PaymentMethod paymentMethod = new PaymentMethod();

        // Assert
        assertNull(paymentMethod.getId());
        assertNull(paymentMethod.getName());
        assertNull(paymentMethod.getGatewayType());
        assertFalse(paymentMethod.isActive());
        assertNull(paymentMethod.getPrice());
        assertFalse(paymentMethod.isFreeForOrderAbove());
        assertNull(paymentMethod.getFreeForOrderAbove());
        assertNull(paymentMethod.getPayments());
    }

    @Test
    void testSetters() {
        // Arrange
        PaymentMethod paymentMethod = new PaymentMethod();
        Long id = 1L;
        String name = "Credit Card";
        PaymentGatewayType gatewayType = PaymentGatewayType.STRIPE_CARD;
        boolean isActive = true;
        BigDecimal price = new BigDecimal("5.99");
        boolean isFreeForOrderAbove = true;
        Set<Payment> payments = new HashSet<>();

        // Act
        paymentMethod.setId(id);
        paymentMethod.setName(name);
        paymentMethod.setGatewayType(gatewayType);
        paymentMethod.setActive(isActive);
        paymentMethod.setPrice(price);
        paymentMethod.setFreeForOrderAbove(isFreeForOrderAbove);
        paymentMethod.setPayments(payments);

        // Assert
        assertEquals(id, paymentMethod.getId());
        assertEquals(name, paymentMethod.getName());
        assertEquals(gatewayType, paymentMethod.getGatewayType());
        assertEquals(isActive, paymentMethod.isActive());
        assertEquals(price, paymentMethod.getPrice());
        assertEquals(isFreeForOrderAbove, paymentMethod.isFreeForOrderAbove());
        assertEquals(payments, paymentMethod.getPayments());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        PaymentMethod paymentMethod1 = PaymentMethod.builder().id(1L).build();
        PaymentMethod paymentMethod2 = PaymentMethod.builder().id(1L).build();
        PaymentMethod paymentMethod3 = PaymentMethod.builder().id(2L).build();

        // Assert
        assertEquals(paymentMethod1, paymentMethod2);
        assertNotEquals(paymentMethod1, paymentMethod3);
        assertEquals(paymentMethod1.hashCode(), paymentMethod2.hashCode());
        assertNotEquals(paymentMethod1.hashCode(), paymentMethod3.hashCode());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String name = "Credit Card";
        PaymentGatewayType gatewayType = PaymentGatewayType.STRIPE_CARD;
        boolean isActive = true;
        BigDecimal price = new BigDecimal("5.99");
        boolean isFreeForOrderAbove = true;
        BigDecimal freeForOrderAbove = new BigDecimal("100.00");
        Set<Payment> payments = new HashSet<>();

        // Act
        PaymentMethod paymentMethod = new PaymentMethod(
                id, name, gatewayType, isActive, price,
                isFreeForOrderAbove, freeForOrderAbove, payments
        );

        // Assert
        assertEquals(id, paymentMethod.getId());
        assertEquals(name, paymentMethod.getName());
        assertEquals(gatewayType, paymentMethod.getGatewayType());
        assertEquals(isActive, paymentMethod.isActive());
        assertEquals(price, paymentMethod.getPrice());
        assertEquals(isFreeForOrderAbove, paymentMethod.isFreeForOrderAbove());
        assertEquals(freeForOrderAbove, paymentMethod.getFreeForOrderAbove());
        assertEquals(payments, paymentMethod.getPayments());
    }

}
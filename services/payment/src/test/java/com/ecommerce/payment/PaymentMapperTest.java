package com.ecommerce.payment;

import com.ecommerce.cod.CODPayment;
import com.ecommerce.feignclient.user.UserResponse;
import com.ecommerce.stripe.StripePayment;
import com.ecommerce.strategy.PaymentGatewayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMapperTest {

    private PaymentMapper paymentMapper;
    private PaymentRequest paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentMapper = new PaymentMapper();

        // Create test user
        UserResponse user = new UserResponse(
                1L, "First name", "Second name", "example@email.com"
        );

        // Create test payment request
        paymentRequest = new PaymentRequest(
                null,
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Create test payment
        payment = new Payment() {};
        payment.setId(1L);
        payment.setOrderId(100L);
        payment.setTotalPrice(new BigDecimal("99.99"));
        payment.setStatus(PaymentStatus.UNPAID);
    }

    @Test
    void toPayment_WithStripeGateway_ShouldReturnStripePayment() {
        // Act
        Payment result = paymentMapper.toPayment(paymentRequest, PaymentGatewayType.STRIPE_CARD);

        // Assert
        assertNotNull(result);
        assertInstanceOf(StripePayment.class, result);
        assertEquals(100L, result.getOrderId());
        assertEquals(new BigDecimal("99.99"), result.getTotalPrice());
    }

    @Test
    void toPayment_WithCODGateway_ShouldReturnCODPayment() {
        // Act
        Payment result = paymentMapper.toPayment(paymentRequest, PaymentGatewayType.CASH_ON_DELIVERY);

        // Assert
        assertNotNull(result);
        assertInstanceOf(CODPayment.class, result);
        assertEquals(100L, result.getOrderId());
        assertEquals(new BigDecimal("99.99"), result.getTotalPrice());
    }

    @Test
    void toResponse_ShouldMapPaymentToPaymentResponse() {
        // Act
        PaymentResponse result = paymentMapper.toResponse(payment);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100L, result.orderId());
        assertEquals(new BigDecimal("99.99"), result.totalPrice());
        assertEquals(PaymentStatus.UNPAID.name(), result.status());
    }
}
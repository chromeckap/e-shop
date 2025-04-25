package com.ecommerce.cod;

import com.ecommerce.payment.Payment;
import com.ecommerce.payment.PaymentStatus;
import com.ecommerce.paymentmethod.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CODPaymentStrategyTest {

    @InjectMocks
    private CODPaymentStrategy codPaymentStrategy;

    @Mock
    private Logger log;

    private CODPayment payment;
    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName("Cash on Delivery");

        payment = new CODPayment();
        payment.setId(1L);
        payment.setOrderId(1001L);
        payment.setTotalPrice(new BigDecimal("150.00"));
        payment.setMethod(paymentMethod);
    }

    @Test
    void processPayment_ShouldSetStatusToCashOnDelivery() {
        // Act
        codPaymentStrategy.processPayment(payment);

        // Assert
        assertEquals(PaymentStatus.CASH_ON_DELIVERY, payment.getStatus(),
                "Payment status should be set to CASH_ON_DELIVERY");
    }

    @Test
    void processPayment_WithNullPayment_ShouldThrowNullPointerException() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> codPaymentStrategy.processPayment(null));

        assertEquals("Platba nesmí být prázdná.", exception.getMessage(),
                "Exception message should match");
    }

    @Test
    void getPaymentUrlById_ShouldThrowUnsupportedOperationException() {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> codPaymentStrategy.getPaymentUrlById(1L));

        assertEquals("Pro dobírku nelze získat URL.", exception.getMessage(),
                "Exception message should match");
    }
}
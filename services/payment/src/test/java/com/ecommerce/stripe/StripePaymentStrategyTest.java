package com.ecommerce.stripe;

import com.ecommerce.payment.PaymentRepository;
import com.ecommerce.payment.PaymentStatus;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripePaymentStrategyTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private StripePaymentStrategy stripePaymentStrategy;

    private StripePayment stripePayment;

    @BeforeEach
    void setUp() {
        stripePayment = StripePayment.builder()
                .build();

        stripePayment.setTotalPrice(BigDecimal.valueOf(100.00));
    }

    @Test
    void testProcessPayment_Success() {
        // Arrange
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            Session mockSession = mock(Session.class);
            when(mockSession.getId()).thenReturn("test-session-id");

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

            // Act
            stripePaymentStrategy.processPayment(stripePayment);

            // Assert
            assertEquals("test-session-id", stripePayment.getSessionId());
            assertEquals(PaymentStatus.UNPAID, stripePayment.getStatus());
        }
    }

    @Test
    void testGetPaymentUrlById_UnpaidStatus() throws StripeException {
        // Arrange
        Long paymentId = 1L;
        stripePayment.setSessionId("test-session-id");
        stripePayment.setStatus(PaymentStatus.UNPAID);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(stripePayment));

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            Session mockSession = mock(Session.class);
            when(mockSession.getUrl()).thenReturn("https://stripe.com/checkout");

            mockedSession.when(() -> Session.retrieve("test-session-id")).thenReturn(mockSession);

            // Act
            String paymentUrl = stripePaymentStrategy.getPaymentUrlById(paymentId);

            // Assert
            assertEquals("https://stripe.com/checkout", paymentUrl);
        }
    }

    @Test
    void testGetPaymentUrlById_PaidStatus() {
        // Arrange
        Long paymentId = 1L;
        stripePayment.setSessionId("test-session-id");
        stripePayment.setStatus(PaymentStatus.PAID);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(stripePayment));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> stripePaymentStrategy.getPaymentUrlById(paymentId));
    }

    @Test
    void testCreateSessionParams() {
        // Reflection-based test to invoke private method
        try {
            BigDecimal totalPrice = BigDecimal.valueOf(100.00);

            // Use reflection to call private method
            java.lang.reflect.Method method = StripePaymentStrategy.class.getDeclaredMethod("createSessionParams", BigDecimal.class);
            method.setAccessible(true);
            SessionCreateParams params = (SessionCreateParams) method.invoke(stripePaymentStrategy, totalPrice);

            // Assert
            assertNotNull(params);
            assertEquals(SessionCreateParams.Mode.PAYMENT, params.getMode());
            assertEquals(SessionCreateParams.Locale.CS, params.getLocale());
            assertEquals("http://localhost:4200", params.getSuccessUrl());
            assertEquals("http://localhost:4200", params.getCancelUrl());
        } catch (Exception e) {
            fail("Error testing private method: " + e.getMessage());
        }
    }
}
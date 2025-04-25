package com.ecommerce.stripe;

import com.ecommerce.feignclient.order.OrderClient;
import com.ecommerce.feignclient.order.OrderResponse;
import com.ecommerce.feignclient.user.UserResponse;
import com.ecommerce.kafka.PaymentConfirmation;
import com.ecommerce.kafka.PaymentProducer;
import com.ecommerce.payment.PaymentRepository;
import com.ecommerce.payment.PaymentStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeWebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentProducer paymentProducer;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private StripeWebhookService stripeWebhookService;

    private StripePayment stripePayment;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        UserResponse userResponse = new UserResponse(
                1L, "First name", "Second name", "example@email.com"
        );

        stripePayment = StripePayment.builder()
                .sessionId("test-session-id")
                .build();
        stripePayment.setOrderId(1L);
        stripePayment.setTotalPrice(BigDecimal.valueOf(100.00));

        orderResponse = new OrderResponse(
                userResponse,
                LocalDateTime.now()
        );
    }

    @Test
    void testHandleWebhook_NullPayload() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> stripeWebhookService.handleWebhook(null, "test-signature"));
    }

    @Test
    void testHandleWebhook_SignatureVerificationFailed() throws SignatureVerificationException {
        // Arrange
        String payload = "test-payload";
        String signatureHeader = "test-signature";
        String webhookSecret = "test-secret";

        // Mock Webhook.constructEvent to throw SignatureVerificationException
        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
            mockedWebhook.when(() -> Webhook.constructEvent(payload, signatureHeader, webhookSecret))
                    .thenThrow(new SignatureVerificationException("Verification failed", "sigHeader"));

            // Act & Assert
            assertThrows(NullPointerException.class,
                    () -> stripeWebhookService.handleWebhook(payload, signatureHeader));
        }
    }
}
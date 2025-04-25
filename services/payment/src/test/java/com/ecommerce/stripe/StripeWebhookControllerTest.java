package com.ecommerce.stripe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StripeWebhookControllerTest {

    @Mock
    private StripeWebhookService stripeWebhookService;

    @InjectMocks
    private StripeWebhookController stripeWebhookController;

    private String testPayload;
    private String testSignatureHeader;

    @BeforeEach
    void setUp() {
        testPayload = "{\"event\": \"test-event\"}";
        testSignatureHeader = "test-signature";
    }

    @Test
    void testHandleWebhook() {
        // Act
        stripeWebhookController.handleWebhook(testPayload, testSignatureHeader);

        // Assert
        verify(stripeWebhookService).handleWebhook(testPayload, testSignatureHeader);
    }
}
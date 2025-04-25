package com.ecommerce.stripe;

import com.stripe.Stripe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class StripeConfigTest {

    @Spy
    private StripeConfig stripeConfig;

    @Test
    void testInit() {
        // Arrange
        String testSecretKey = "test-secret-key";
        ReflectionTestUtils.setField(stripeConfig, "secretKey", testSecretKey);

        // Act
        stripeConfig.init();

        // Assert
        assertEquals(testSecretKey, Stripe.apiKey);
    }
}
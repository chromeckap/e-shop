package com.ecommerce.paymentmethod;

import com.ecommerce.strategy.PaymentGatewayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class PaymentMethodMapperTest {

    private PaymentMethodMapper paymentMethodMapper;

    @BeforeEach
    void setUp() {
        paymentMethodMapper = new PaymentMethodMapper();
    }

    @Test
    void toPaymentMethod_ShouldMapAllFields() {
        // Arrange
        PaymentMethodRequest request = new PaymentMethodRequest(
                1L, // ID is not used in mapping
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Act
        PaymentMethod result = paymentMethodMapper.toPaymentMethod(request);

        // Assert
        assertNull(result.getId()); // ID is not set by mapper
        assertEquals("Credit Card", result.getName());
        assertEquals(PaymentGatewayType.STRIPE_CARD, result.getGatewayType());
        assertTrue(result.isActive());
        assertEquals(new BigDecimal("5.99"), result.getPrice());
        assertTrue(result.isFreeForOrderAbove());
        assertEquals(new BigDecimal("100.00"), result.getFreeForOrderAbove());
        assertNull(result.getPayments()); // Payments are not set by mapper
    }

    @Test
    void toResponse_ShouldMapAllFields() {
        // Arrange
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .payments(new HashSet<>())
                .build();

        // Create expected gateway type map
        Map<String, String> gatewayTypeMap = new HashMap<>();
        gatewayTypeMap.put("name", "STRIPE_CARD");
        gatewayTypeMap.put("description", "Stripe Card");

        // Using try-with-resources to properly close the mockStatic context
        try (MockedStatic<PaymentGatewayType> mockedStatic = mockStatic(PaymentGatewayType.class)) {
            // Mock the static method since it's called inside the mapper
            mockedStatic.when(() -> PaymentGatewayType.getType(any(PaymentMethod.class)))
                    .thenReturn(gatewayTypeMap);

            // Act
            PaymentMethodResponse result = paymentMethodMapper.toResponse(paymentMethod);

            // Assert
            assertEquals(1L, result.id());
            assertEquals("Credit Card", result.name());
            assertEquals(gatewayTypeMap, result.gatewayType());
            assertTrue(result.isActive());
            assertEquals(new BigDecimal("5.99"), result.price());
            assertTrue(result.isFreeForOrderAbove());
            assertEquals(new BigDecimal("100.00"), result.freeForOrderAbove());
        }
    }
}
package com.ecommerce.strategy;

import com.ecommerce.paymentmethod.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentGatewayTypeTest {

    @Test
    void getName_ShouldReturnCorrectName() {
        // Assert
        assertEquals("Stripe karta", PaymentGatewayType.STRIPE_CARD.getName());
        assertEquals("Dobírka", PaymentGatewayType.CASH_ON_DELIVERY.getName());
    }

    @Test
    void getAll_ShouldReturnAllPaymentGatewayTypes() {
        // Act
        List<Map<String, String>> types = PaymentGatewayType.getAll();

        // Assert
        assertEquals(2, types.size());

        // Check STRIPE_CARD entry
        Map<String, String> stripeType = types.stream()
                .filter(map -> "STRIPE_CARD".equals(map.get("type")))
                .findFirst()
                .orElse(null);
        assertNotNull(stripeType);
        assertEquals("Stripe karta", stripeType.get("name"));

        // Check CASH_ON_DELIVERY entry
        Map<String, String> codType = types.stream()
                .filter(map -> "CASH_ON_DELIVERY".equals(map.get("type")))
                .findFirst()
                .orElse(null);
        assertNotNull(codType);
        assertEquals("Dobírka", codType.get("name"));
    }

    @Test
    void getType_ShouldReturnCorrectTypeMap() {
        // Arrange
        PaymentMethod stripePaymentMethod = PaymentMethod.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .build();

        PaymentMethod codPaymentMethod = PaymentMethod.builder()
                .id(2L)
                .name("Cash on Delivery")
                .gatewayType(PaymentGatewayType.CASH_ON_DELIVERY)
                .isActive(true)
                .price(new BigDecimal("0.00"))
                .build();

        // Act
        Map<String, String> stripeTypeMap = PaymentGatewayType.getType(stripePaymentMethod);
        Map<String, String> codTypeMap = PaymentGatewayType.getType(codPaymentMethod);

        // Assert
        assertEquals("STRIPE_CARD", stripeTypeMap.get("type"));
        assertEquals("Stripe karta", stripeTypeMap.get("name"));

        assertEquals("CASH_ON_DELIVERY", codTypeMap.get("type"));
        assertEquals("Dobírka", codTypeMap.get("name"));
    }

    @Test
    void enumValues_ShouldContainAllExpectedTypes() {
        // Act
        PaymentGatewayType[] types = PaymentGatewayType.values();

        // Assert
        assertEquals(2, types.length);
        assertArrayEquals(
                new PaymentGatewayType[]{PaymentGatewayType.STRIPE_CARD, PaymentGatewayType.CASH_ON_DELIVERY},
                types
        );
    }

    @Test
    void valueOf_ShouldReturnCorrectEnumConstant() {
        // Act & Assert
        assertEquals(PaymentGatewayType.STRIPE_CARD, PaymentGatewayType.valueOf("STRIPE_CARD"));
        assertEquals(PaymentGatewayType.CASH_ON_DELIVERY, PaymentGatewayType.valueOf("CASH_ON_DELIVERY"));
    }

    @Test
    void valueOf_WithInvalidName_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> PaymentGatewayType.valueOf("INVALID_TYPE"));
    }
}
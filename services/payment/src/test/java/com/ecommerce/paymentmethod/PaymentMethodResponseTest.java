package com.ecommerce.paymentmethod;

import com.ecommerce.strategy.PaymentGatewayType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodResponseTest {

    @Test
    void builder_ShouldCreateResponseWithAllFields() {
        // Arrange
        Map<String, String> gatewayTypeMap = new HashMap<>();
        gatewayTypeMap.put("name", "STRIPE_CARD");
        gatewayTypeMap.put("description", "Stripe Card");

        // Act
        PaymentMethodResponse response = PaymentMethodResponse.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(gatewayTypeMap)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        // Assert
        assertEquals(1L, response.id());
        assertEquals("Credit Card", response.name());
        assertEquals(gatewayTypeMap, response.gatewayType());
        assertTrue(response.isActive());
        assertEquals(new BigDecimal("5.99"), response.price());
        assertTrue(response.isFreeForOrderAbove());
        assertEquals(new BigDecimal("100.00"), response.freeForOrderAbove());
    }

    @Test
    void response_WithSameValues_ShouldBeEqual() {
        // Arrange
        Map<String, String> gatewayTypeMap = new HashMap<>();
        gatewayTypeMap.put("name", "STRIPE_CARD");
        gatewayTypeMap.put("description", "Stripe Card");

        PaymentMethodResponse response1 = PaymentMethodResponse.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(gatewayTypeMap)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        PaymentMethodResponse response2 = PaymentMethodResponse.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(gatewayTypeMap)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        // Act & Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void response_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        Map<String, String> gatewayTypeMap1 = new HashMap<>();
        gatewayTypeMap1.put("name", "STRIPE_CARD");
        gatewayTypeMap1.put("description", "Stripe Card");

        Map<String, String> gatewayTypeMap2 = new HashMap<>();
        gatewayTypeMap2.put("name", "CASH_ON_DELIVERY");
        gatewayTypeMap2.put("description", "Cash on Delivery");

        PaymentMethodResponse response1 = PaymentMethodResponse.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(gatewayTypeMap1)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        PaymentMethodResponse response2 = PaymentMethodResponse.builder()
                .id(2L) // Different ID
                .name("Cash on Delivery")
                .gatewayType(gatewayTypeMap2)
                .isActive(true)
                .price(new BigDecimal("0.00"))
                .isFreeForOrderAbove(false)
                .freeForOrderAbove(null)
                .build();

        // Act & Assert
        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void response_AccessorsShouldReturnCorrectValues() {
        // Arrange
        Long id = 1L;
        String name = "Credit Card";
        Map<String, String> gatewayTypeMap = new HashMap<>();
        gatewayTypeMap.put("name", "STRIPE_CARD");
        gatewayTypeMap.put("description", "Stripe Card");
        boolean isActive = true;
        BigDecimal price = new BigDecimal("5.99");
        boolean isFreeForOrderAbove = true;
        BigDecimal freeForOrderAbove = new BigDecimal("100.00");

        // Act
        PaymentMethodResponse response = PaymentMethodResponse.builder()
                .id(id)
                .name(name)
                .gatewayType(gatewayTypeMap)
                .isActive(isActive)
                .price(price)
                .isFreeForOrderAbove(isFreeForOrderAbove)
                .freeForOrderAbove(freeForOrderAbove)
                .build();

        // Assert
        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(gatewayTypeMap, response.gatewayType());
        assertEquals(isActive, response.isActive());
        assertEquals(price, response.price());
        assertEquals(isFreeForOrderAbove, response.isFreeForOrderAbove());
        assertEquals(freeForOrderAbove, response.freeForOrderAbove());
    }
}
package com.ecommerce.orderitem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("Should calculate total price correctly")
    void calculateTotalPrice_ShouldMultiplyPriceByQuantity() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(new BigDecimal("129.99"));
        orderItem.setQuantity(3);

        // Act
        BigDecimal totalPrice = orderItem.calculateTotalPrice();

        // Assert
        assertEquals(new BigDecimal("389.97"), totalPrice);
    }

    @Test
    @DisplayName("Should calculate total price as zero when quantity is zero")
    void calculateTotalPrice_ShouldReturnZeroWhenQuantityIsZero() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(new BigDecimal("129.99"));
        orderItem.setQuantity(0);

        // Act
        BigDecimal totalPrice = orderItem.calculateTotalPrice();

        // Assert
        assertEquals(new BigDecimal("0.00"), totalPrice);
    }

    @Test
    @DisplayName("Should build order item with all fields")
    void builder_ShouldCreateOrderItemWithAllFields() {
        // Arrange
        Long id = 1L;
        Long productId = 100L;
        String name = "Test Product";
        BigDecimal price = new BigDecimal("99.99");
        int quantity = 2;
        Map<String, String> values = new HashMap<>();
        values.put("color", "blue");
        values.put("size", "M");

        // Act
        OrderItem orderItem = OrderItem.builder()
                .id(id)
                .productId(productId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .values(values)
                .build();

        // Assert
        assertEquals(id, orderItem.getId());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(name, orderItem.getName());
        assertEquals(price, orderItem.getPrice());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(values, orderItem.getValues());
        assertNull(orderItem.getOrder());
    }

    @Test
    @DisplayName("Should create equal order items with same id")
    void equals_ShouldReturnTrueForSameId() {
        // Arrange
        OrderItem orderItem1 = OrderItem.builder().id(1L).productId(100L).name("Product 1").price(new BigDecimal("10.00")).quantity(1).build();
        OrderItem orderItem2 = OrderItem.builder().id(1L).productId(200L).name("Product 2").price(new BigDecimal("20.00")).quantity(2).build();

        // Act & Assert
        assertEquals(orderItem1, orderItem2);
        assertEquals(orderItem1.hashCode(), orderItem2.hashCode());
    }

    @Test
    @DisplayName("Should create different order items with different ids")
    void equals_ShouldReturnFalseForDifferentIds() {
        // Arrange
        OrderItem orderItem1 = OrderItem.builder().id(1L).productId(100L).name("Product").price(new BigDecimal("10.00")).quantity(1).build();
        OrderItem orderItem2 = OrderItem.builder().id(2L).productId(100L).name("Product").price(new BigDecimal("10.00")).quantity(1).build();

        // Act & Assert
        assertNotEquals(orderItem1, orderItem2);
        assertNotEquals(orderItem1.hashCode(), orderItem2.hashCode());
    }
}
package com.ecommerce.orderitem;

import com.ecommerce.feignclient.product.PurchaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemMapperTest {

    private final OrderItemMapper orderItemMapper = new OrderItemMapper();

    @Test
    @DisplayName("Should map PurchaseResponse to OrderItem")
    void toOrderItem_ShouldMapCorrectly() {
        // Arrange
        Map<String, String> values = new HashMap<>();
        values.put("color", "blue");
        values.put("size", "M");

        PurchaseResponse purchaseResponse = new PurchaseResponse(
                123L, "Test Product", new BigDecimal("99.99"), 2, new BigDecimal("199.98"), values
        );

        // Act
        OrderItem result = orderItemMapper.toOrderItem(purchaseResponse);

        // Assert
        assertNotNull(result);
        assertEquals(purchaseResponse.productId(), result.getProductId());
        assertEquals(purchaseResponse.name(), result.getName());
        assertEquals(purchaseResponse.price(), result.getPrice());
        assertEquals(purchaseResponse.quantity(), result.getQuantity());
        assertEquals(purchaseResponse.values(), result.getValues());
        assertNull(result.getId());
        assertNull(result.getOrder());
    }

    @Test
    @DisplayName("Should throw NullPointerException when PurchaseResponse is null")
    void toOrderItem_ShouldThrowExceptionWhenNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderItemMapper.toOrderItem(null));
    }

    @Test
    @DisplayName("Should map OrderItem to OrderItemResponse")
    void toResponse_ShouldMapCorrectly() {
        // Arrange
        Long id = 1L;
        Long productId = 123L;
        String name = "Test Product";
        BigDecimal price = new BigDecimal("99.99");
        int quantity = 2;
        Map<String, String> values = new HashMap<>();
        values.put("color", "blue");
        values.put("size", "M");

        OrderItem orderItem = OrderItem.builder()
                .id(id)
                .productId(productId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .values(values)
                .build();

        // Act
        OrderItemResponse result = orderItemMapper.toResponse(orderItem);

        // Assert
        assertNotNull(result);
        assertEquals(orderItem.getId(), result.id());
        assertEquals(orderItem.getProductId(), result.productId());
        assertEquals(orderItem.getName(), result.name());
        assertEquals(orderItem.getPrice(), result.price());
        assertEquals(orderItem.getQuantity(), result.quantity());
        assertEquals(orderItem.calculateTotalPrice(), result.totalPrice());
        assertEquals(orderItem.getValues(), result.values());
    }

    @Test
    @DisplayName("Should throw NullPointerException when OrderItem is null")
    void toResponse_ShouldThrowExceptionWhenNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderItemMapper.toResponse(null));
    }

    @Test
    @DisplayName("Should handle null values map in OrderItem")
    void toResponse_ShouldHandleNullValues() {
        // Arrange
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(123L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .quantity(2)
                .values(null)
                .build();

        // Act
        OrderItemResponse result = orderItemMapper.toResponse(orderItem);

        // Assert
        assertNotNull(result);
        assertNull(result.values());
    }
}
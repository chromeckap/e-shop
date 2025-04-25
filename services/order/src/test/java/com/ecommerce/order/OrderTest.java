package com.ecommerce.order;

import com.ecommerce.orderitem.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setOrderItems(new ArrayList<>());
        order.setAdditionalCosts(new HashMap<>());

        orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setPrice(new BigDecimal("100.00"));
        orderItem1.setQuantity(2);

        orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setPrice(new BigDecimal("50.00"));
        orderItem2.setQuantity(1);
    }

    @Test
    @DisplayName("Should add order item correctly")
    void addOrderItem_ShouldAddItem() {
        // Act
        order.addOrderItem(orderItem1);

        // Assert
        assertEquals(1, order.getOrderItems().size());
        assertEquals(order, orderItem1.getOrder());
        assertTrue(order.getOrderItems().contains(orderItem1));
    }

    @Test
    @DisplayName("Should add multiple order items correctly")
    void addOrderItem_ShouldAddMultipleItems() {
        // Act
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        // Assert
        assertEquals(2, order.getOrderItems().size());
        assertEquals(order, orderItem1.getOrder());
        assertEquals(order, orderItem2.getOrder());
        assertTrue(order.getOrderItems().contains(orderItem1));
        assertTrue(order.getOrderItems().contains(orderItem2));
    }

    @Test
    @DisplayName("Should add additional cost correctly")
    void addAdditionalCost_ShouldAddCost() {
        // Arrange
        String costName = "Shipping";
        BigDecimal costAmount = new BigDecimal("15.00");

        // Act
        order.addAdditionalCost(costName, costAmount);

        // Assert
        Map<String, BigDecimal> additionalCosts = order.getAdditionalCosts();
        assertTrue(additionalCosts.containsKey(costName));
        assertEquals(costAmount, additionalCosts.get(costName));
    }

    @Test
    @DisplayName("Should add multiple additional costs correctly")
    void addAdditionalCost_ShouldAddMultipleCosts() {
        // Arrange
        String costName1 = "Shipping";
        BigDecimal costAmount1 = new BigDecimal("15.00");
        String costName2 = "Payment Fee";
        BigDecimal costAmount2 = new BigDecimal("5.00");

        // Act
        order.addAdditionalCost(costName1, costAmount1);
        order.addAdditionalCost(costName2, costAmount2);

        // Assert
        Map<String, BigDecimal> additionalCosts = order.getAdditionalCosts();
        assertEquals(2, additionalCosts.size());
        assertTrue(additionalCosts.containsKey(costName1));
        assertTrue(additionalCosts.containsKey(costName2));
        assertEquals(costAmount1, additionalCosts.get(costName1));
        assertEquals(costAmount2, additionalCosts.get(costName2));
    }

    @Test
    @DisplayName("Should calculate total price correctly with only order items")
    void calculateTotalPrice_ShouldCalculateWithOnlyItems() {
        // Arrange
        // Mock the calculateTotalPrice method for OrderItem
        OrderItem mockItem1 = new OrderItem() {
            @Override
            public BigDecimal calculateTotalPrice() {
                return new BigDecimal("200.00"); // 100 * 2
            }
        };

        OrderItem mockItem2 = new OrderItem() {
            @Override
            public BigDecimal calculateTotalPrice() {
                return new BigDecimal("50.00"); // 50 * 1
            }
        };

        order.addOrderItem(mockItem1);
        order.addOrderItem(mockItem2);

        // Act
        BigDecimal totalPrice = order.calculateTotalPrice();

        // Assert
        assertEquals(new BigDecimal("250.00"), totalPrice);
    }

    @Test
    @DisplayName("Should calculate total price correctly with only additional costs")
    void calculateTotalPrice_ShouldCalculateWithOnlyAdditionalCosts() {
        // Arrange
        order.addAdditionalCost("Shipping", new BigDecimal("15.00"));
        order.addAdditionalCost("Payment Fee", new BigDecimal("5.00"));

        // Act
        BigDecimal totalPrice = order.calculateTotalPrice();

        // Assert
        assertEquals(new BigDecimal("20.00"), totalPrice);
    }

    @Test
    @DisplayName("Should calculate total price correctly with both order items and additional costs")
    void calculateTotalPrice_ShouldCalculateWithItemsAndCosts() {
        // Arrange
        // Mock the calculateTotalPrice method for OrderItem
        OrderItem mockItem1 = new OrderItem() {
            @Override
            public BigDecimal calculateTotalPrice() {
                return new BigDecimal("200.00"); // 100 * 2
            }
        };

        OrderItem mockItem2 = new OrderItem() {
            @Override
            public BigDecimal calculateTotalPrice() {
                return new BigDecimal("50.00"); // 50 * 1
            }
        };

        order.addOrderItem(mockItem1);
        order.addOrderItem(mockItem2);

        order.addAdditionalCost("Shipping", new BigDecimal("15.00"));
        order.addAdditionalCost("Payment Fee", new BigDecimal("5.00"));

        // Act
        BigDecimal totalPrice = order.calculateTotalPrice();

        // Assert
        assertEquals(new BigDecimal("270.00"), totalPrice);
    }

    @Test
    @DisplayName("Should calculate total price correctly when empty")
    void calculateTotalPrice_ShouldReturnZeroWhenEmpty() {
        // Act
        BigDecimal totalPrice = order.calculateTotalPrice();

        // Assert
        assertEquals(BigDecimal.ZERO, totalPrice);
    }
}
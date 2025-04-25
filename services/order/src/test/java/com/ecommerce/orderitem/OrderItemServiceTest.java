package com.ecommerce.orderitem;

import com.ecommerce.feignclient.product.PurchaseResponse;
import com.ecommerce.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemService orderItemService;

    @Captor
    private ArgumentCaptor<OrderItem> orderItemCaptor;

    private PurchaseResponse purchaseResponse;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        // Setup test data
        Map<String, String> values = new HashMap<>();
        values.put("color", "blue");
        values.put("size", "M");

        purchaseResponse = new PurchaseResponse(
                123L, "Test Product", new BigDecimal("99.99"), 2, new BigDecimal("199.98"), values
        );

        order = new Order();
        order.setId(1L);
        order.setOrderItems(new ArrayList<>());

        orderItem = OrderItem.builder()
                .productId(purchaseResponse.productId())
                .name(purchaseResponse.name())
                .price(purchaseResponse.price())
                .quantity(purchaseResponse.quantity())
                .values(purchaseResponse.values())
                .build();
    }

    @Test
    @DisplayName("Should create and save order item successfully")
    void createOrderItem_ShouldCreateAndSaveOrderItem() {
        // Arrange
        when(orderItemMapper.toOrderItem(purchaseResponse)).thenReturn(orderItem);

        // Act
        orderItemService.createOrderItem(purchaseResponse, order);

        // Assert
        verify(orderItemMapper).toOrderItem(purchaseResponse);
        verify(orderItemRepository).save(orderItemCaptor.capture());

        OrderItem savedOrderItem = orderItemCaptor.getValue();
        assertNotNull(savedOrderItem);
        assertEquals(orderItem, savedOrderItem);
        assertEquals(order, savedOrderItem.getOrder());

        // Verify the item was added to the order
        assertEquals(1, order.getOrderItems().size());
        assertTrue(order.getOrderItems().contains(orderItem));
    }

    @Test
    @DisplayName("Should throw NullPointerException when PurchaseResponse is null")
    void createOrderItem_ShouldThrowExceptionWhenPurchaseResponseIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderItemService.createOrderItem(null, order));
        verify(orderItemMapper, never()).toOrderItem(any());
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NullPointerException when Order is null")
    void createOrderItem_ShouldThrowExceptionWhenOrderIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderItemService.createOrderItem(purchaseResponse, null));
        verify(orderItemMapper, never()).toOrderItem(any());
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should properly link order and order item")
    void createOrderItem_ShouldLinkOrderAndOrderItem() {
        // Arrange
        when(orderItemMapper.toOrderItem(purchaseResponse)).thenReturn(orderItem);

        // Act
        orderItemService.createOrderItem(purchaseResponse, order);

        // Assert
        // Verify the bidirectional relationship is set correctly
        assertEquals(order, orderItem.getOrder());
        assertTrue(order.getOrderItems().contains(orderItem));
    }
}
package com.ecommerce.order;

import com.ecommerce.address.AddressRequest;
import com.ecommerce.feignclient.product.PurchaseRequest;
import com.ecommerce.orderitem.OrderItem;
import com.ecommerce.orderitem.OrderItemMapper;
import com.ecommerce.orderitem.OrderItemResponse;
import com.ecommerce.userdetails.UserDetails;
import com.ecommerce.userdetails.UserDetailsMapper;
import com.ecommerce.userdetails.UserDetailsRequest;
import com.ecommerce.userdetails.UserDetailsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private UserDetailsMapper userDetailsMapper;

    @InjectMocks
    private OrderMapper orderMapper;

    private OrderRequest orderRequest;
    private Order order;
    private UserDetails userDetails;
    private UserDetailsResponse userDetailsResponse;
    private OrderItem orderItem;
    private OrderItemResponse orderItemResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        AddressRequest addressRequest = new AddressRequest("Street 123", "City", "12345");

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                1L, "John", "Doe", "john@example.com", addressRequest
        );

        userDetailsResponse = UserDetailsResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        Set<PurchaseRequest> products = new HashSet<>();
        products.add(new PurchaseRequest(1L, 2));

        orderRequest = new OrderRequest(1L, userDetailsRequest, products, 1L, 1L);

        userDetails = new UserDetails();
        userDetails.setId(1L);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setPrice(new BigDecimal("100.00"));
        orderItem.setQuantity(2);

        orderItemResponse = OrderItemResponse.builder()
                .id(1L)
                .productId(1L)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .quantity(2)
                .totalPrice(new BigDecimal("200.00"))
                .build();

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setCreateTime(now);
        order.setUserDetails(userDetails);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        Map<String, BigDecimal> additionalCosts = new HashMap<>();
        additionalCosts.put("Shipping", new BigDecimal("15.00"));
        additionalCosts.put("Payment Fee", new BigDecimal("5.00"));
        order.setAdditionalCosts(additionalCosts);
    }

    @Test
    @DisplayName("Should map OrderRequest to Order")
    void toOrder_ShouldMapCorrectly() {
        // Act
        Order result = orderMapper.toOrder(orderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(orderRequest.id(), result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());
    }

    @Test
    @DisplayName("Should throw NullPointerException when OrderRequest is null")
    void toOrder_ShouldThrowExceptionWhenNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderMapper.toOrder(null));
    }

    @Test
    @DisplayName("Should map Order to OrderResponse")
    void toResponse_ShouldMapCorrectly() {
        // Arrange
        when(userDetailsMapper.toResponse(userDetails)).thenReturn(userDetailsResponse);
        when(orderItemMapper.toResponse(orderItem)).thenReturn(orderItemResponse);

        // Act
        OrderResponse result = orderMapper.toResponse(order);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.id());
        assertEquals(userDetailsResponse, result.userDetails());
        assertEquals(1, result.items().size());
        assertTrue(result.items().contains(orderItemResponse));
        assertEquals(order.getAdditionalCosts(), result.additionalCosts());
        assertEquals(order.getStatus().name(), result.status());
        assertEquals(order.getCreateTime(), result.createTime());

        verify(userDetailsMapper).toResponse(userDetails);
        verify(orderItemMapper).toResponse(orderItem);
    }

    @Test
    @DisplayName("Should handle null values in Order when mapping to OrderResponse")
    void toResponse_ShouldHandleNullValues() {
        // Arrange
        Order emptyOrder = new Order();
        emptyOrder.setId(1L);
        emptyOrder.setStatus(OrderStatus.CREATED);
        emptyOrder.setCreateTime(now);

        // Based on the error, it seems the actual mapper tries to map null userDetails
        // Let's mock that behavior
        when(userDetailsMapper.toResponse(null)).thenReturn(null);

        // Act
        OrderResponse result = orderMapper.toResponse(emptyOrder);

        // Assert
        assertNotNull(result);
        assertEquals(emptyOrder.getId(), result.id());
        assertNull(result.userDetails());
        assertTrue(result.items().isEmpty());
        assertTrue(result.additionalCosts().isEmpty());
        assertEquals(emptyOrder.getStatus().name(), result.status());
        assertEquals(emptyOrder.getCreateTime(), result.createTime());

        // The actual implementation calls toResponse with null
        verify(userDetailsMapper).toResponse(null);
        verify(orderItemMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should throw NullPointerException when Order is null in toResponse")
    void toResponse_ShouldThrowExceptionWhenNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderMapper.toResponse(null));
    }

    @Test
    @DisplayName("Should map Order to OrderOverviewResponse")
    void toOverviewResponse_ShouldMapCorrectly() {
        // Arrange
        when(userDetailsMapper.toResponse(userDetails)).thenReturn(userDetailsResponse);

        // Act
        OrderOverviewResponse result = orderMapper.toOverviewResponse(order);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.id());
        assertEquals(userDetailsResponse, result.userDetails());
        assertEquals(order.getStatus().name(), result.status());
        assertEquals(order.getCreateTime(), result.createTime());

        verify(userDetailsMapper).toResponse(userDetails);
    }

    @Test
    @DisplayName("Should throw NullPointerException when Order is null in toOverviewResponse")
    void toOverviewResponse_ShouldThrowExceptionWhenNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderMapper.toOverviewResponse(null));
    }
}
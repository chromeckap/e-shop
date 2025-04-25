package com.ecommerce.order;

import com.ecommerce.address.AddressRequest;
import com.ecommerce.feignclient.product.PurchaseRequest;
import com.ecommerce.settings.Constants;
import com.ecommerce.userdetails.UserDetailsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private OrderOverviewResponse orderOverviewResponse;
    private Long orderId;
    private Long userId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        userId = 1L;
        now = LocalDateTime.now();

        AddressRequest addressRequest = new AddressRequest("Street 123", "City", "12345");

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                userId, "John", "Doe", "john@example.com", addressRequest
        );

        Set<PurchaseRequest> products = new HashSet<>();
        products.add(new PurchaseRequest(1L, 2));

        orderRequest = new OrderRequest(orderId, userDetailsRequest, products, 1L, 1L);

        orderResponse = OrderResponse.builder()
                .id(orderId)
                .status(OrderStatus.CREATED.name())
                .createTime(now)
                .totalPrice(new BigDecimal("220.00"))
                .build();

        orderOverviewResponse = OrderOverviewResponse.builder()
                .id(orderId)
                .status(OrderStatus.CREATED.name())
                .createTime(now)
                .totalPrice(new BigDecimal("220.00"))
                .build();
    }

    @Test
    @DisplayName("Should get order by id")
    void getOrderById_ShouldReturnOrderResponse() {
        // Arrange
        when(orderService.getOrderById(orderId)).thenReturn(orderResponse);

        // Act
        ResponseEntity<OrderResponse> response = orderController.getOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).getOrderById(orderId);
    }

    @Test
    @DisplayName("Should get all orders with paging")
    void getAllOrders_ShouldReturnPagedOrders() {
        // Arrange
        int pageNumber = 0;
        int pageSize = 10;
        String direction = "DESC";
        String attribute = "createTime";

        Sort sort = Sort.by(Sort.Direction.fromString(direction), attribute);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        List<OrderOverviewResponse> orderResponses = new ArrayList<>();
        orderResponses.add(orderOverviewResponse);
        Page<OrderOverviewResponse> pagedResponse = new PageImpl<>(orderResponses, pageRequest, orderResponses.size());

        when(orderService.getAllOrders(any(PageRequest.class))).thenReturn(pagedResponse);

        // Act
        ResponseEntity<Page<OrderOverviewResponse>> response = orderController.getAllOrders(
                pageNumber, pageSize, direction, attribute);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pagedResponse, response.getBody());
        assertEquals(1, response.getBody().getTotalElements());

        verify(orderService).getAllOrders(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should get orders by user id")
    void getOrdersByUserId_ShouldReturnUserOrders() {
        // Arrange
        List<OrderOverviewResponse> orderResponses = new ArrayList<>();
        orderResponses.add(orderOverviewResponse);

        when(orderService.getOrdersByUserId(userId)).thenReturn(orderResponses);

        // Act
        ResponseEntity<List<OrderOverviewResponse>> response = orderController.getOrdersByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponses, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(orderService).getOrdersByUserId(userId);
    }

    @Test
    @DisplayName("Should create order")
    void createOrder_ShouldReturnCreatedOrderId() {
        // Arrange
        when(orderService.createOrder(orderRequest)).thenReturn(orderId);

        // Act
        ResponseEntity<Long> response = orderController.createOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderId, response.getBody());

        verify(orderService).createOrder(orderRequest);
    }

    @Test
    @DisplayName("Should delete order by id")
    void deleteOrderById_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = orderController.deleteOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(orderService).deleteOrderById(orderId);
    }

    @Test
    @DisplayName("Should update order status")
    void updateOrderStatus_ShouldReturnAccepted() {
        // Arrange
        String newStatus = OrderStatus.DELIVERED.name();

        // Act
        ResponseEntity<Void> response = orderController.updateOrderStatus(orderId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        verify(orderService).updateOrderStatus(orderId, newStatus);
    }

}
package com.ecommerce.order;

import com.ecommerce.address.AddressRequest;
import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.feignclient.deliverymethod.DeliveryMethodClient;
import com.ecommerce.feignclient.deliverymethod.DeliveryMethodResponse;
import com.ecommerce.feignclient.payment.PaymentClient;
import com.ecommerce.feignclient.payment.PaymentRequest;
import com.ecommerce.feignclient.paymentmethod.PaymentMethodClient;
import com.ecommerce.feignclient.paymentmethod.PaymentMethodResponse;
import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.PurchaseRequest;
import com.ecommerce.feignclient.product.PurchaseResponse;
import com.ecommerce.feignclient.shoppingcart.CartClient;
import com.ecommerce.kafka.OrderProducer;
import com.ecommerce.orderitem.OrderItemService;
import com.ecommerce.security.SecurityValidator;
import com.ecommerce.userdetails.UserDetailsRequest;
import com.ecommerce.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductClient productClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentMethodClient paymentMethodClient;

    @Mock
    private DeliveryMethodClient deliveryMethodClient;

    @Mock
    private CartClient cartClient;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private OrderProducer orderProducer;


    @Mock
    private OrderItemService orderItemService;

    @Mock
    private SecurityValidator securityValidator;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private Long orderId;
    private Long userId;
    private Order order;
    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private OrderOverviewResponse orderOverviewResponse;
    private Set<PurchaseRequest> purchaseRequests;
    private Set<PurchaseResponse> purchaseResponses;
    private PaymentMethodResponse paymentMethodResponse;
    private DeliveryMethodResponse deliveryMethodResponse;

    @BeforeEach
    void setUp() {
        orderId = 1L;
        userId = 1L;

        // Setup test data
        order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CREATED);
        order.setCreateTime(LocalDateTime.now());
        order.setAdditionalCosts(new HashMap<>());

        orderResponse = OrderResponse.builder()
                .id(orderId)
                .status(OrderStatus.CREATED.name())
                .build();

        orderOverviewResponse = OrderOverviewResponse.builder()
                .id(orderId)
                .status(OrderStatus.CREATED.name())
                .build();

        AddressRequest addressRequest = new AddressRequest("Street 123", "City", "12345");

        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(
                userId, "John", "Doe", "john@example.com", addressRequest
        );

        purchaseRequests = new HashSet<>();
        PurchaseRequest purchaseRequest = new PurchaseRequest(1L, 2);
        purchaseRequests.add(purchaseRequest);

        purchaseResponses = new HashSet<>();
        PurchaseResponse purchaseResponse = new PurchaseResponse(1L, "Product 1", new BigDecimal("100.00"), 2, new BigDecimal("200.00"), null);
        purchaseResponses.add(purchaseResponse);

        paymentMethodResponse = new PaymentMethodResponse(
                "Credit Card", new BigDecimal("200.00"), true, new BigDecimal("10.00")
        );

        deliveryMethodResponse = new DeliveryMethodResponse(
                "Standard Delivery", new BigDecimal("200.00"), false, new BigDecimal("15.00")
        );

        orderRequest = new OrderRequest(
                null, userDetailsRequest, purchaseRequests, 1L, 1L
        );
    }

    @Test
    @DisplayName("Should find order by id successfully")
    void findOrderEntityById_ShouldFindOrderSuccessfully() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Order result = orderService.findOrderEntityById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found")
    void findOrderEntityById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderEntityById(orderId));
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should throw NullPointerException when order id is null")
    void findOrderEntityById_ShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> orderService.findOrderEntityById(null));
        verify(orderRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should get order by id successfully")
    void getOrderById_ShouldReturnOrderResponse() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderResponse, result);
        verify(orderRepository).findById(orderId);
        verify(orderMapper).toResponse(order);
    }

    @Test
    @DisplayName("Should get all orders with paging")
    void getAllOrders_ShouldReturnPagedOrders() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        List<Order> orders = Collections.singletonList(order);
        Page<Order> ordersPage = new PageImpl<>(orders, pageRequest, orders.size());

        when(orderRepository.findAll(pageRequest)).thenReturn(ordersPage);
        when(orderMapper.toOverviewResponse(order)).thenReturn(orderOverviewResponse);

        // Act
        Page<OrderOverviewResponse> result = orderService.getAllOrders(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderOverviewResponse, result.getContent().getFirst());
        verify(orderRepository).findAll(pageRequest);
        verify(orderMapper).toOverviewResponse(order);
    }

    @Test
    @DisplayName("Should get orders by user id")
    void getOrdersByUserId_ShouldReturnUserOrders() {
        // Arrange
        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.findAllByUserDetails_UserId(userId)).thenReturn(orders);
        when(orderMapper.toOverviewResponse(order)).thenReturn(orderOverviewResponse);

        // Act
        List<OrderOverviewResponse> result = orderService.getOrdersByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderOverviewResponse, result.getFirst());
        verify(orderRepository).findAllByUserDetails_UserId(userId);
        verify(orderMapper).toOverviewResponse(order);
    }

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Arrange
        when(productClient.purchaseProducts(purchaseRequests)).thenReturn(purchaseResponses);
        when(orderMapper.toOrder(orderRequest)).thenReturn(order);
        when(paymentMethodClient.getPaymentMethodById(1L)).thenReturn(paymentMethodResponse);
        when(deliveryMethodClient.getDeliveryMethodById(1L)).thenReturn(deliveryMethodResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Long result = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result);

        verify(securityValidator).validateUserAccess(userId);
        verify(productClient).purchaseProducts(purchaseRequests);
        verify(orderMapper).toOrder(orderRequest);
        verify(paymentMethodClient).getPaymentMethodById(1L);
        verify(deliveryMethodClient).getDeliveryMethodById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(userDetailsService).manageUserDetails(eq(orderRequest.userDetails()), any(Order.class));
        verify(cartClient).clearCartByUserId(userId);
        verify(paymentClient).createPayment(any(PaymentRequest.class));
        verify(orderProducer).sendOrderConfirmation(any());

        // Verify additionalCosts
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        Map<String, BigDecimal> additionalCosts = savedOrder.getAdditionalCosts();
        assertTrue(additionalCosts.containsKey("Credit Card"));
        assertTrue(additionalCosts.containsKey("Standard Delivery"));
    }

    @Test
    @DisplayName("Should apply free payment for orders above threshold")
    void createOrder_ShouldApplyFreePaymentWhenAboveThreshold() {
        // Arrange
        // Make product price well above the payment threshold
        PurchaseResponse expensiveProduct = new PurchaseResponse(
                1L, "Expensive Product", new BigDecimal("600.00"), 1, new BigDecimal("600.00"), null
        );
        Set<PurchaseResponse> expensiveProducts = Collections.singleton(expensiveProduct);

        when(productClient.purchaseProducts(purchaseRequests)).thenReturn(expensiveProducts);
        when(orderMapper.toOrder(orderRequest)).thenReturn(order);
        when(paymentMethodClient.getPaymentMethodById(1L)).thenReturn(paymentMethodResponse);
        when(deliveryMethodClient.getDeliveryMethodById(1L)).thenReturn(deliveryMethodResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.createOrder(orderRequest);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        Map<String, BigDecimal> additionalCosts = savedOrder.getAdditionalCosts();

        // Payment should be free since product price (600) > threshold (200)
        assertEquals(BigDecimal.ZERO, additionalCosts.get("Credit Card"));
        // Delivery is not free by default based on the mocked response (isFreeForOrderAbove=false)
        assertEquals(new BigDecimal("200.00"), additionalCosts.get("Standard Delivery"));
    }

    @Test
    @DisplayName("Should apply free delivery for orders above threshold")
    void createOrder_ShouldApplyFreeDeliveryWhenAboveThreshold() {
        // Arrange
        // Update the deliveryMethodResponse to have isFreeForOrderAbove=true
        DeliveryMethodResponse freeDeliveryResponse = new DeliveryMethodResponse(
                "Standard Delivery", new BigDecimal("200.00"), true, new BigDecimal("15.00")
        );

        // Make product price well above the delivery threshold
        PurchaseResponse expensiveProduct = new PurchaseResponse(
                1L, "Expensive Product", new BigDecimal("300.00"), 1, new BigDecimal("300.00"), null
        );
        Set<PurchaseResponse> expensiveProducts = Collections.singleton(expensiveProduct);

        when(productClient.purchaseProducts(purchaseRequests)).thenReturn(expensiveProducts);
        when(orderMapper.toOrder(orderRequest)).thenReturn(order);
        when(paymentMethodClient.getPaymentMethodById(1L)).thenReturn(paymentMethodResponse);
        when(deliveryMethodClient.getDeliveryMethodById(1L)).thenReturn(freeDeliveryResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.createOrder(orderRequest);

        // Assert
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        Map<String, BigDecimal> additionalCosts = savedOrder.getAdditionalCosts();

        // Payment should be free (above threshold)
        assertEquals(BigDecimal.ZERO, additionalCosts.get("Credit Card"));
        // Delivery should be free (above threshold)
        assertEquals(BigDecimal.ZERO, additionalCosts.get("Standard Delivery"));
    }

    @Test
    @DisplayName("Should delete order by id")
    void deleteOrderById_ShouldDeleteOrder() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrderById(orderId);

        // Assert
        verify(orderRepository).findById(orderId);
        verify(orderRepository).delete(order);
    }

    @Test
    @DisplayName("Should update order status")
    void updateOrderStatus_ShouldUpdateStatus() {
        // Arrange
        String newStatus = OrderStatus.DELIVERED.name();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.updateOrderStatus(orderId, newStatus);

        // Assert
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertEquals(OrderStatus.DELIVERED, savedOrder.getStatus());
    }
}
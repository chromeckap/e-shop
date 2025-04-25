package com.ecommerce.order;

import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.feignclient.deliverymethod.DeliveryMethodClient;
import com.ecommerce.feignclient.deliverymethod.DeliveryMethodResponse;
import com.ecommerce.feignclient.payment.PaymentClient;
import com.ecommerce.feignclient.payment.PaymentRequest;
import com.ecommerce.feignclient.paymentmethod.PaymentMethodClient;
import com.ecommerce.feignclient.paymentmethod.PaymentMethodResponse;
import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.PurchaseResponse;
import com.ecommerce.feignclient.shoppingcart.CartClient;
import com.ecommerce.kafka.OrderConfirmation;
import com.ecommerce.kafka.OrderProducer;
import com.ecommerce.orderitem.OrderItemService;
import com.ecommerce.security.SecurityValidator;
import com.ecommerce.userdetails.UserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final PaymentClient paymentClient;
    private final PaymentMethodClient paymentMethodClient;
    private final DeliveryMethodClient deliveryMethodClient;
    private final CartClient cartClient;
    private final OrderItemService orderItemService;
    private final UserDetailsService userDetailsService;
    private final OrderProducer orderProducer;
    private final SecurityValidator securityValidator;

    /**
     * Finds an order entity by its ID.
     *
     * @param id the order id (must not be null)
     * @return the found Order entity
     * @throws OrderNotFoundException if no order exists with the given id
     */
    @Transactional(readOnly = true)
    public Order findOrderEntityById(Long id) {
        Objects.requireNonNull(id, "ID objednávky nesmí být prázdné.");
        log.debug("Finding order with ID: {}", id);

        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Objednávka s ID %s nebyl nalezen.", id)
                ));
    }

    /**
     * Retrieves an order response DTO by order ID.
     *
     * @param id the order id (must not be null)
     * @return the OrderResponse DTO
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Objects.requireNonNull(id, "ID objednávky nesmí být prázdné.");
        log.debug("Getting order response for order with ID: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        securityValidator.validateUserAccess(Long.valueOf(authentication.getName()));

        Order order = this.findOrderEntityById(id);
        return orderMapper.toResponse(order);
    }

    /**
     * Retrieves all orders with paging.
     *
     * @param pageRequest the paging information
     * @return a page of OrderOverviewResponse DTOs
     */
    @Transactional(readOnly = true)
    public Page<OrderOverviewResponse> getAllOrders(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "Požadavek na stránkování nesmí být prázdný.");
        log.debug("Fetching all orders with page request: {}", pageRequest);

        return orderRepository.findAll(pageRequest)
                .map(orderMapper::toOverviewResponse);
    }

    public List<OrderOverviewResponse> getOrdersByUserId(Long userId) {
        Objects.requireNonNull(userId, "ID uživatele nesmí být prázdné.");
        log.debug("Fetching orders by user ID: {}", userId);

        return orderRepository.findAllByUserDetails_UserId(userId).stream()
                .map(orderMapper::toOverviewResponse)
                .toList();
    }

    /**
     * Creates a new order.
     *
     * @param request the OrderRequest DTO (must not be null)
     * @return the id of the newly created order
     */
    @Transactional
    public Long createOrder(OrderRequest request) {
        Objects.requireNonNull(request, "Požadavek objednávky nesmí být prázdný.");
        log.debug("Creating order with request: {}", request);

        securityValidator.validateUserAccess(request.userDetails().id());

        Set<PurchaseResponse> purchasedProducts = productClient.purchaseProducts(request.products());
        Order order = orderMapper.toOrder(request);

        BigDecimal totalItemsPrice = this.calculateTotalPrice(purchasedProducts);
        this.processPaymentCost(order, request, totalItemsPrice);
        this.processDeliveryCost(order, request, totalItemsPrice);

        Order savedOrder = orderRepository.save(order);
        log.info("Order successfully saved with ID: {}", savedOrder.getId());

        userDetailsService.manageUserDetails(request.userDetails(), savedOrder);

        purchasedProducts.forEach(product -> {
            log.debug("Creating order item for product {} for order {}", product.productId(), savedOrder.getId());
            orderItemService.createOrderItem(product, order);
        });

        cartClient.clearCartByUserId(request.userDetails().id());
        log.debug("Cleared cart for user with ID: {}", request.userDetails().id());

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .totalPrice(savedOrder.calculateTotalPrice())
                .orderId(savedOrder.getId())
                .user(request.userDetails())
                .OrderCreateTime(savedOrder.getCreateTime().format(DateTimeFormatter.ofPattern("d.M.yyyy")))
                .products(purchasedProducts)
                .paymentMethodId(request.paymentMethodId())
                .build();
        paymentClient.createPayment(paymentRequest);
        log.info("Payment created for order ID: {}", savedOrder.getId());

        OrderConfirmation orderConfirmation = OrderConfirmation.builder()
                .orderId(savedOrder.getId())
                .orderCreateTime(savedOrder.getCreateTime().format(DateTimeFormatter.ofPattern("d.M.yyyy")))
                .totalPrice(savedOrder.calculateTotalPrice())
                .user(request.userDetails())
                .products(purchasedProducts)
                .additionalCosts(savedOrder.getAdditionalCosts())
                .build();
        orderProducer.sendOrderConfirmation(orderConfirmation);
        log.info("Order confirmation sent for order ID: {}", savedOrder.getId());

        return savedOrder.getId();
    }

    /**
     * Deletes an order by its id.
     *
     * @param id the order id (must not be null)
     */
    @Transactional
    public void deleteOrderById(Long id) {
        Objects.requireNonNull(id, "ID objednávky nesmí být prázdné.");
        log.debug("Deleting order with ID: {}", id);

        Order order = this.findOrderEntityById(id);

        orderRepository.delete(order);
        log.info("Objednávka úspěšně smazána.");
    }

    /**
     * Processes the payment cost by adding the cost as an additional order cost.
     *
     * @param order             the order entity to update
     * @param request           the order request containing payment details
     */
    private void processPaymentCost(Order order, OrderRequest request, BigDecimal totalItemsPrice) {
        Objects.requireNonNull(order, "Objednávka nesmí být prázdná.");
        Objects.requireNonNull(request, "Požadavek objednávky nesmí být prázdný.");
        log.debug("Processing payment cost for order. Request: {}", request);

        PaymentMethodResponse paymentMethod = paymentMethodClient.getPaymentMethodById(request.paymentMethodId());
        BigDecimal freeThreshold = paymentMethod.freeForOrderAbove();
        BigDecimal paymentFee = paymentMethod.price();

        if (paymentMethod.isFreeForOrderAbove() && totalItemsPrice.compareTo(freeThreshold) >= 0)
            paymentFee = BigDecimal.ZERO;

        order.addAdditionalCost(paymentMethod.name(), paymentFee);
        log.debug("Payment cost processed. Payment method: {}, fee applied: {}", paymentMethod.name(), paymentFee);
    }

    /**
     * Processes the delivery cost by adding the cost as an additional order cost.
     *
     * @param order             the order entity to update
     * @param request           the order request containing payment details
     */
    private void processDeliveryCost(Order order, OrderRequest request, BigDecimal totalItemsPrice) {
        Objects.requireNonNull(order, "Objednávka nesmí být prázdná.");
        Objects.requireNonNull(request, "Požadavek objednávky nesmí být prázdný.");
        log.debug("Processing delivery cost for order. Request: {}", request);

        DeliveryMethodResponse deliveryMethod = deliveryMethodClient.getDeliveryMethodById(request.deliveryMethodId());
        BigDecimal freeThreshold = deliveryMethod.freeForOrderAbove();
        BigDecimal deliveryFee = deliveryMethod.price();

        if (deliveryMethod.isFreeForOrderAbove() && totalItemsPrice.compareTo(freeThreshold) >= 0)
            deliveryFee = BigDecimal.ZERO;

        order.addAdditionalCost(deliveryMethod.name(), deliveryFee);
        log.debug("Delivery cost processed. Delivery method: {}, fee applied: {}", deliveryMethod.name(), deliveryFee);
    }

    /**
     * Calculates the total price of purchased products.
     *
     * @param purchasedProducts the set of purchased products
     * @return the total price as a BigDecimal
     */
    private BigDecimal calculateTotalPrice(Set<PurchaseResponse> purchasedProducts) {
        return purchasedProducts.stream()
                .map(PurchaseResponse::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Updates the status for order by ID.
     *
     * @param id                the ID of order
     * @param status            the status to be changed
     */
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Objects.requireNonNull(id, "ID objednávky nesmí být prázdné.");
        Objects.requireNonNull(status, "Status nesmí být prázdný.");
        log.debug("Fetching order with id: {}", id);

        Order order = this.findOrderEntityById(id);
        order.setStatus(OrderStatus.valueOf(status));

        orderRepository.save(order);
        log.info("Status {} was successfully assigned to order with ID: {}", status, id);
    }
}

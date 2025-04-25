package com.ecommerce.orderitem;

import com.ecommerce.order.Order;
import com.ecommerce.feignclient.product.PurchaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    /**
     * Creates an order item from a given purchase response and adds it to the provided order.
     *
     * @param response the purchase response containing product details (must not be null)
     * @param order the order to which the new order item will be added (must not be null)
     * @throws NullPointerException if either response or order is null
     */
    @Transactional
    public void createOrderItem(PurchaseResponse response, Order order) {
        Objects.requireNonNull(response, "Požadavek na zakoupení nesmí být prázdný.");
        Objects.requireNonNull(order, "Objednávka nesmí být prázdná.");

        log.debug("Creating order item for order with ID: {} based on purchase response: {}", order.getId(), response);

        OrderItem orderItem = orderItemMapper.toOrderItem(response);
        order.addOrderItem(orderItem);

        orderItemRepository.save(orderItem);
        log.info("Order item for product {} successfully added to order with ID: {}", response.productId(), order.getId());
    }
}

package com.ecommerce.orderitem;

import com.ecommerce.feignclient.product.PurchaseResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderItemMapper {
    public OrderItem toOrderItem(@NonNull PurchaseResponse request) {
        log.debug("Mapping PurchaseResponse to OrderItem: {}", request);
        return OrderItem.builder()
                .productId(request.productId())
                .name(request.name())
                .price(request.price())
                .quantity(request.quantity())
                .values(request.values())
                .build();
    }

    public OrderItemResponse toResponse(@NonNull OrderItem orderItem) {
        log.debug("Mapping OrderItem to OrderItemResponse: {}", orderItem);
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .name(orderItem.getName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.calculateTotalPrice())
                .values(orderItem.getValues())
                .build();
    }
}

package com.ecommerce.order;

import com.ecommerce.orderitem.OrderItemMapper;
import com.ecommerce.orderitem.OrderItemResponse;
import com.ecommerce.userdetails.UserDetailsMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;
    private final UserDetailsMapper userDetailsMapper;

    public Order toOrder(@NonNull OrderRequest request) {
        log.debug("Mapping OrderRequest to Order: {}", request);
        return Order.builder()
                .id(request.id())
                .status(OrderStatus.CREATED)
                .build();
    }

    public OrderResponse toResponse(@NonNull Order order) {
        log.debug("Mapping Order to OrderResponse: {}", order);
        return OrderResponse.builder()
                .id(order.getId())
                .userDetails(userDetailsMapper.toResponse(order.getUserDetails()))
                .items(this.mapOrderItemsFor(order))
                .additionalCosts(
                        Optional.ofNullable(order.getAdditionalCosts())
                                .orElse(Collections.emptyMap())
                )
                .totalPrice(order.calculateTotalPrice())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateDate() != null ? order.getCreateTime() : null)
                .status(order.getStatus().name())
                .build();
    }

    public OrderOverviewResponse toOverviewResponse(@NonNull Order order) {
        log.debug("Mapping Order to OrderOverviewResponse: {}", order);
        return OrderOverviewResponse.builder()
                .id(order.getId())
                .userDetails(userDetailsMapper.toResponse(order.getUserDetails()))
                .totalPrice(order.calculateTotalPrice())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateDate() != null ? order.getCreateTime() : null)
                .status(order.getStatus().name())
                .build();
    }

    private Set<OrderItemResponse> mapOrderItemsFor(Order order) {
        return Optional.ofNullable(order.getOrderItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(orderItemMapper::toResponse)
                .collect(Collectors.toSet());
    }
}

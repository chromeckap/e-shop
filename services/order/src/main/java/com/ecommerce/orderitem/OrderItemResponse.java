package com.ecommerce.orderitem;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record OrderItemResponse(
        Long id,
        Long productId,
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal totalPrice,
        Map<String, String> values
) {}

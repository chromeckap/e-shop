package com.ecommerce.kafka.order;

import java.math.BigDecimal;
import java.util.Map;

public record Product(
        Long productId,
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal totalPrice,
        Map<String, String> values
) {}

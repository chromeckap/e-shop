package com.ecommerce.kafka.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public record OrderConfirmation(
        BigDecimal totalPrice,
        User user,
        Set<Product> products,
        Map<String, BigDecimal> additionalCosts,
        Long orderId,
        String orderDate
) {}

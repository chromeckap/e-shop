package com.ecommerce.deliverymethod;

import com.ecommerce.strategy.CourierType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record DeliveryMethodResponse(
        Long id,
        String name,
        Map<String, String> courierType,
        boolean isActive,
        BigDecimal price,
        boolean isFreeForOrderAbove,
        BigDecimal freeForOrderAbove
) {}
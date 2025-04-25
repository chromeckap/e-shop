package com.ecommerce.paymentmethod;

import com.ecommerce.strategy.PaymentGatewayType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record PaymentMethodResponse(
        Long id,
        String name,
        Map<String, String> gatewayType,
        boolean isActive,
        BigDecimal price,
        boolean isFreeForOrderAbove,
        BigDecimal freeForOrderAbove
) {}

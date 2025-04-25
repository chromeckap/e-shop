package com.ecommerce.kafka;

import com.ecommerce.feignclient.product.PurchaseResponse;
import com.ecommerce.userdetails.UserDetailsRequest;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Builder
public record OrderConfirmation(
        BigDecimal totalPrice,
        UserDetailsRequest user,
        Set<PurchaseResponse> products,
        Map<String, BigDecimal> additionalCosts,
        Long orderId,
        String orderCreateTime
) {}

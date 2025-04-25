package com.ecommerce.order;

import com.ecommerce.orderitem.OrderItemResponse;
import com.ecommerce.userdetails.UserDetailsResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Builder
public record OrderResponse(
        Long id,
        UserDetailsResponse userDetails,
        Set<OrderItemResponse> items,
        Map<String, BigDecimal> additionalCosts,
        BigDecimal totalPrice,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        String status
) {}
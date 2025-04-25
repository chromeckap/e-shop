package com.ecommerce.payment;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal totalPrice,
        String status
) {}
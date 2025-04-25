package com.ecommerce.kafka;

import com.ecommerce.feignclient.user.UserResponse;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentConfirmation(
        Long orderId,
        BigDecimal totalPrice,
        String paymentMethodName,
        UserResponse user,
        String sessionUri,
        String OrderCreateTime
) {}

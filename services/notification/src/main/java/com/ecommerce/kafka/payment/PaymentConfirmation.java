package com.ecommerce.kafka.payment;

import com.ecommerce.kafka.order.User;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentConfirmation(
        Long orderId,
        BigDecimal totalPrice,
        String paymentMethodName,
        User user,
        String sessionUri,
        String OrderCreateTime
) {}

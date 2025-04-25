package com.ecommerce.feignclient.payment;

import com.ecommerce.feignclient.product.PurchaseResponse;
import com.ecommerce.userdetails.UserDetailsRequest;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record PaymentRequest(
        Long id,
        Long orderId,
        BigDecimal totalPrice,
        Long paymentMethodId,
        UserDetailsRequest user,
        String OrderCreateTime,
        Set<PurchaseResponse> products
) {}

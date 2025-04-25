package com.ecommerce.feignclient.paymentmethod;

import java.math.BigDecimal;

public record PaymentMethodResponse(
        String name,
        BigDecimal price,
        boolean isFreeForOrderAbove,
        BigDecimal freeForOrderAbove
) {}

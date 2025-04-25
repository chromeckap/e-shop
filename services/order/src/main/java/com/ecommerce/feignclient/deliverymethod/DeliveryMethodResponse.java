package com.ecommerce.feignclient.deliverymethod;

import java.math.BigDecimal;

public record DeliveryMethodResponse(
        String name,
        BigDecimal price,
        boolean isFreeForOrderAbove,
        BigDecimal freeForOrderAbove
) {}

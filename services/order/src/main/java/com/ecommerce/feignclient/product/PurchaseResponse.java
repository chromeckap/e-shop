package com.ecommerce.feignclient.product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public record PurchaseResponse(
        Long productId,
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal totalPrice,
        Map<String, String> values
) {}

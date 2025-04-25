package com.ecommerce.variant.purchase;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record PurchaseResponse(
        Long productId,
        Long variantId,
        String name,
        String primaryImagePath,
        BigDecimal price,
        int quantity,
        int availableQuantity,
        boolean isAvailable,
        BigDecimal totalPrice,
        Map<String, String> values
) {}

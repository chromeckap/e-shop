package com.ecommerce.product;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Builder
public record ProductOverviewResponse (
        Long id,
        String name,
        BigDecimal price,
        BigDecimal basePrice,
        boolean isPriceEqual,
        boolean isVisible,
        Set<Long> categoryIds,
        List<Long> relatedProductIds,
        String primaryImagePath
) {}

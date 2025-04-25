package com.ecommerce.feignclient.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductOverviewResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal basePrice,
        boolean isPriceEqual,
        boolean isVisible,
        Set<Long> categoryIds,
        List<Long> relatedProductIds,
        String primaryImagePath
) {}

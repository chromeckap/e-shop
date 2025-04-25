package com.ecommerce.product;

import com.ecommerce.attribute.AttributeResponse;
import com.ecommerce.variant.VariantResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Builder
public record ProductResponse (
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal basePrice,
        boolean isPriceEqual,
        boolean isVisible,
        Set<VariantResponse> variants,
        Set<ProductOverviewResponse> relatedProducts,
        Set<Long> categoryIds,
        Set<AttributeResponse> attributes,
        List<String> imagePaths
) {}

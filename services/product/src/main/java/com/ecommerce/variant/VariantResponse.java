package com.ecommerce.variant;

import com.ecommerce.attributevalue.AttributeValueResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record VariantResponse (
        Long id,
        String sku,
        BigDecimal basePrice,
        BigDecimal discountedPrice,
        int quantity,
        boolean quantityUnlimited,
        Map<Long, AttributeValueResponse> attributeValues
) {}

package com.ecommerce.product;

import com.ecommerce.attribute.AttributeResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record FilterRangesResponse(
        BigDecimal lowPrice,
        BigDecimal maxPrice,
        Set<AttributeResponse> attributes
) {}

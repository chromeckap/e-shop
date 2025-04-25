package com.ecommerce.attribute;

import lombok.Builder;

@Builder
public record AttributeOverviewResponse(
        Long id,
        String name
) {}

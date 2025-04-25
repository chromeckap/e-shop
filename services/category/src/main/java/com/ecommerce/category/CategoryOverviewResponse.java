package com.ecommerce.category;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoryOverviewResponse(
        Long id,
        String name,
        String description,
        List<CategoryOverviewResponse> children
) {}

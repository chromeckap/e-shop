package com.ecommerce.category;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoryResponse (
        Long id,
        String name,
        String description,
        CategoryOverviewResponse parent,
        List<CategoryOverviewResponse> children
) {}

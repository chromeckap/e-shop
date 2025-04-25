package com.ecommerce.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProductRequest (
        Long id,
        @NotBlank(message = "Jméno produktu nesmí být prázdné.")
        @NotNull(message = "Jméno produktu nesmí být prázdné.")
        @Size(max = 255, message = "Jméno produktu nesmí být delší než 255 znaků.")
        String name,
        String description,
        @NotNull
        boolean isVisible,
        Set<Long> categoryIds,
        Set<Long> attributeIds,
        Set<Long> relatedProductIds
) {}

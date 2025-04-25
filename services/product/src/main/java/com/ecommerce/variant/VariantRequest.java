package com.ecommerce.variant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record VariantRequest(
        Long id,
        @NotNull(message = "ID produktu nesmí být prázdné.")
        Long productId,
        @NotBlank(message = "SKU nesmí být prázdné.")
        @Size(max = 50, message = "SKU nesmí být delší než 50 znaků.")
        String sku,
        @NotNull(message = "Základní cena nesmí být prázdná.")
        @Min(value = 0, message = "Základní cena musí být kladné číslo.")
        BigDecimal basePrice,
        @Min(value = 0, message = "Zlevněná cena musí být kladné číslo.")
        BigDecimal discountedPrice,
        @Min(value = 0, message = "Počet kusů musí být kladné číslo.")
        int quantity,
        boolean quantityUnlimited,
        Set<Long> attributeValueIds
) {}

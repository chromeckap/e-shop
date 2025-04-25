package com.ecommerce.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record ProductSpecificationRequest(
        @PositiveOrZero(message = "Nejnižší cena musí být nula a výše.")
        BigDecimal lowPrice,
        @Positive(message = "Maximální cena musí být pozitivní.")
        BigDecimal maxPrice,
        Set<Long> attributeValueIds
) {}

package eshop.backend.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record VariantResponse(
     Long id,
     int quantity,
     boolean isAvailable,
     BigDecimal basePrice,
     BigDecimal discountedPrice,
     int discountPercentage
     //Set<AttributeValueResponse> values
) {}

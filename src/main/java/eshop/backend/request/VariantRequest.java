package eshop.backend.request;

import java.math.BigDecimal;
import java.util.Set;

public record VariantRequest (
    Long id,
    String sku,
    boolean unlimitedQuantity,
    Long productId,
    BigDecimal basePrice,
    BigDecimal discountedPrice,
    Set<Long> attributeValueIds
) { }



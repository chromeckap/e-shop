package eshop.backend.request;

import java.math.BigDecimal;
import java.util.Set;

public record ProductSearchRequest(
        BigDecimal lowPrice,
        BigDecimal maxPrice,
        Set<Long> attributeValueIds
) {}
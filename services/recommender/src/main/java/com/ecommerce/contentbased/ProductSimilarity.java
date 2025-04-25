package com.ecommerce.contentbased;

import lombok.Builder;

@Builder
record ProductSimilarity(
        Long productId,
        double similarity
) {}

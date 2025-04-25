package com.ecommerce.cartitem;

import lombok.Builder;

@Builder
public record CartItemRequest(
        Long variantId,
        int quantity
) {}

package com.ecommerce.variant.purchase;

public record CartItemRequest(
        Long variantId,
        int quantity
) {}

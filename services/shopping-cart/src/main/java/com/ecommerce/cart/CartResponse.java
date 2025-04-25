package com.ecommerce.cart;

import com.ecommerce.feignclient.product.PurchaseResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartResponse(
        Long id,
        Long userId,
        BigDecimal totalPrice,
        List<PurchaseResponse> items
) {}

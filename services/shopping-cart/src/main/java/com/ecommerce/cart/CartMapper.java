package com.ecommerce.cart;

import com.ecommerce.cartitem.CartItemRequest;
import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.PurchaseResponse;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartMapper {
    private final ProductClient productClient;

    public Cart toCart(@Nonnull Long userId) {
        log.debug("Creating new Cart for userId: {}", userId);
        return Cart.builder()
                .userId(userId)
                .build();
    }

    public CartResponse toResponse(@NonNull Cart cart) {
        List<PurchaseResponse> items = this.mapItemsFor(cart);

        log.debug("Mapping Cart to CartResponse: {}", cart);
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .totalPrice(this.calculateTotalPriceFor(items))
                .items(items)
                .build();
    }

    private BigDecimal calculateTotalPriceFor(List<PurchaseResponse> items) {
        return items.stream()
                .map(PurchaseResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<PurchaseResponse> mapItemsFor(Cart cart) {
        List<CartItemRequest> items = cart.getCartItems().stream().map(
                item -> CartItemRequest.builder()
                        .variantId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build()
        ).toList();

        return productClient.getVariantsByCartItems(items);
    }

}

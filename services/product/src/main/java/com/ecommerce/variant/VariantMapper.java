package com.ecommerce.variant;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.attributevalue.AttributeValueMapper;
import com.ecommerce.attributevalue.AttributeValueResponse;
import com.ecommerce.variant.purchase.PurchaseResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class VariantMapper {
    private final AttributeValueMapper attributeValueMapper;

    public Variant toVariant(@NonNull VariantRequest request) {
        log.debug("Mapping VariantRequest to Variant: {}", request);
        return Variant.builder()
                .sku(request.sku())
                .basePrice(request.basePrice())
                .discountedPrice(request.discountedPrice())
                .quantity(request.quantity())
                .quantityUnlimited(request.quantityUnlimited())
                .build();
    }

    public VariantResponse toResponse(@NonNull Variant variant) {
        log.debug("Mapping Variant to VariantResponse: {}", variant);
        return VariantResponse.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .basePrice(variant.getBasePrice())
                .discountedPrice(variant.getDiscountedPrice())
                .quantity(variant.getQuantity())
                .quantityUnlimited(variant.isQuantityUnlimited())
                .attributeValues(this.mapAttributeValuesFor(variant))
                .build();
    }

    public PurchaseResponse toPurchaseResponse(@NonNull Variant variant, int quantity) {
        log.debug("Mapping Variant to PurchaseResponse: {} with quantity {}", variant, quantity);
        return PurchaseResponse.builder()
                .productId(variant.getProduct().getId())
                .variantId(variant.getId())
                .primaryImagePath(this.mapPrimaryImagePathFor(variant))
                .name(variant.getProduct().getName())
                .price(variant.getPrice())
                .quantity(quantity)
                .availableQuantity(variant.getQuantity())
                .isAvailable(this.mapQuantityAvailableFor(variant, quantity))
                .totalPrice(variant.getPrice()
                        .multiply(BigDecimal.valueOf(quantity))
                )
                .values(this.mapAttributeValuesToMapFor(variant))
                .build();
    }

    private boolean mapQuantityAvailableFor(Variant variant, int quantity) {
        return variant.isQuantityUnlimited() || variant.getQuantity() >= quantity;
    }

    private Map<Long, AttributeValueResponse> mapAttributeValuesFor(Variant variant) {
        return Optional.ofNullable(variant.getValues())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                        av -> av.getAttribute().getId(),
                        attributeValueMapper::toResponse,
                        (existing, replacement) -> existing
                ));
    }

    private Map<String, String> mapAttributeValuesToMapFor(Variant variant) {
        return Optional.ofNullable(variant.getValues())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                        attributeValue -> attributeValue.getAttribute().getName(),
                        AttributeValue::getValue
                ));
    }

    private String mapPrimaryImagePathFor(Variant variant) {
        return Optional.ofNullable(variant.getProduct().getImages())
                .filter(images -> !images.isEmpty())
                .map(images -> images.getFirst().getImagePath())
                .orElse(null);
    }
}

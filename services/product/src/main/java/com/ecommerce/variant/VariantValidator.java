package com.ecommerce.variant;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.exception.QuantityOutOfStockException;
import com.ecommerce.exception.VariantNotFoundException;
import com.ecommerce.variant.purchase.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class VariantValidator {
    private final VariantRepository variantRepository;

    public void validateAllVariantsExist(Set<Long> variantIds) {
        int count = variantRepository.countByIds(variantIds);

        if (variantIds.isEmpty()) {
            log.debug("Skipping validation, no variant IDs provided.");
            return;
        }

        log.debug("Validating existence of variants: {}", variantIds);
        if (variantIds.size() != count)
            throw new VariantNotFoundException("Jedna nebo více variant neexistuje.");
    }

    public void validateProductNumberVariants(Variant variant) {
        log.debug("Validating number of variants available for product: {}", variant);
        int maxVariants = calculateMaxVariants(variant);

        if (variant.getProduct().getVariants().size() + 1 > maxVariants) {
            throw new IllegalArgumentException("Počet variant překračuje povolený limit: " + maxVariants);
        }
    }

    
    public void validateAvailableQuantity(Variant variant, PurchaseRequest request) {
        log.debug("Validating available quantity for variant: {}, requested: {}", variant.getId(), request.quantity());
        if (variant.isQuantityUnlimited())
            return;

        if (variant.getQuantity() < request.quantity())
            throw new QuantityOutOfStockException(
                    String.format("Nedostatečný počet kusů pro %s", variant.getId())
            );
    }

    public void validateSetNotEmpty(Set<PurchaseRequest> request) {
        log.debug("Validating request is not empty: {}", request);
        if (request == null || request.isEmpty())
            throw new IllegalArgumentException("Požadavek na nákup nesmí být prázdný.");

    }

    public void validateTotalPriceIsNotOver(List<Variant> existingVariants, Map<Long, PurchaseRequest> variantRequests, BigDecimal maxTotalPrice) {
        BigDecimal sum = existingVariants.stream()
                .map(variant -> {
                    PurchaseRequest request = variantRequests.get(variant.getId());
                    if (request == null) return BigDecimal.ZERO;
                    return variant.getPrice().multiply(BigDecimal.valueOf(request.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(maxTotalPrice) > 0)
            throw new IllegalArgumentException(
                    String.format("Celková cena variant přesahuje maximální částku %s Kč.", maxTotalPrice)
            );
    }


    private int calculateMaxVariants(Variant variant) {
        Set<Attribute> attributes = variant.getProduct().getAttributes();

        if (attributes.isEmpty())
            return 1;

        return attributes.stream()
                .map(attribute -> attribute.getValues().size())
                .reduce(1, (a, b) -> a * b);
    }
}

package com.ecommerce.product;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.SelfRelatingProductException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductValidator {
    private final ProductRepository productRepository;

    public void validateNoSelfReference(Set<Long> relatedProductIds, Long productId) {
        log.debug("Product {} is trying to reference to ID Products {}.", productId, relatedProductIds);
        if (relatedProductIds.contains(productId))
            throw new SelfRelatingProductException(
                    String.format("Produkt s ID %s nemůže být relačně propojen sám se sebou.", productId)
            );
    }

    public void validateAllProductsExist(Set<Long> relatedProductIds) {
        if (relatedProductIds.isEmpty()) {
            log.debug("Skipping validation, no related product IDs provided.");
            return;
        }

        int count = productRepository.countByIds(relatedProductIds);

        log.debug("Validating existence of related products: {}", relatedProductIds);
        if (relatedProductIds.size() != count)
            throw new ProductNotFoundException("Jeden nebo více relačních produktů neexistuje.");
    }
}

package com.ecommerce.relatedproduct;

import com.ecommerce.product.Product;
import com.ecommerce.product.ProductRepository;
import com.ecommerce.product.ProductRequest;
import com.ecommerce.product.ProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatedProductService {
    private final ProductRepository productRepository;
    private final ProductValidator productValidator;

    /**
     * Processes and retrieves related products for a given product.
     *
     * @param product the product for which related products are being processed
     * @param request the product request containing related product IDs
     * @return a set of related product entities
     */
    @Transactional
    public Set<Product> processRelatedProducts(Product product, ProductRequest request) {
        Objects.requireNonNull(product, "Produkt nesmí být prázdný");
        Objects.requireNonNull(request, "Požadavek produktu nesmí být prázdný");

        Set<Long> relatedProductIds = request.relatedProductIds();

        if (relatedProductIds == null || relatedProductIds.isEmpty()) {
            log.debug("No related product IDs provided for product ID {}.", product.getId());
            return Collections.emptySet();
        }

        productValidator.validateNoSelfReference(relatedProductIds, product.getId());
        productValidator.validateAllProductsExist(relatedProductIds);

        List<Product> relatedProducts = productRepository.findAllById(relatedProductIds);
        log.info("Successfully processed {} related products for product ID {}.", relatedProducts.size(), product.getId());
        return Set.copyOf(relatedProducts);
    }
}

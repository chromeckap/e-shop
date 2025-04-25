package com.ecommerce.product;

import com.ecommerce.attribute.AttributeMapper;
import com.ecommerce.attribute.AttributeResponse;
import com.ecommerce.productimage.ProductImage;
import com.ecommerce.variant.VariantMapper;
import com.ecommerce.variant.VariantResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductMapper {
    private final VariantMapper variantMapper;
    private final AttributeMapper attributeMapper;
    private final ProductPriceService priceService;

    public Product toProduct(@NonNull ProductRequest request) {
        log.debug("Mapping ProductRequest to Product: {}", request);
        return Product.builder()
                .id(request.id())
                .name(request.name())
                .description(request.description())
                .isVisible(request.isVisible())
                .build();
    }

    public ProductOverviewResponse toOverviewResponse(@NonNull Product product) {
        log.debug("Mapping Product to ProductOverviewResponse: {}", product);
        return ProductOverviewResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(priceService.getCheapestVariantPrice(product))
                .basePrice(priceService.getCheapestVariantBasePrice(product))
                .isPriceEqual(priceService.isVariantsPricesEqual(product))
                .isVisible(product.isVisible())
                .categoryIds(product.getCategoryIds())
                .relatedProductIds(this.mapRelatedProductIdsFor(product))
                .primaryImagePath(this.mapPrimaryImagePath(product))
                .build();
    }

    public ProductResponse toResponse(@NonNull Product product) {
        log.debug("Mapping Product to ProductResponse: {}", product);
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(priceService.getCheapestVariantPrice(product))
                .basePrice(priceService.getCheapestVariantBasePrice(product))
                .isPriceEqual(priceService.isVariantsPricesEqual(product))
                .isVisible(product.isVisible())
                .variants(this.mapVariantsFor(product))
                .categoryIds(product.getCategoryIds())
                .relatedProducts(this.mapRelatedProductsFor(product))
                .attributes(this.mapAttributesFor(product))
                .imagePaths(this.mapProductImages(product))
                .build();
    }

    private Set<VariantResponse> mapVariantsFor(Product product) {
        return Optional.ofNullable(product.getVariants())
                .map(variants -> variants.stream()
                        .map(variantMapper::toResponse)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    private Set<ProductOverviewResponse> mapRelatedProductsFor(Product product) {
        return Optional.ofNullable(product.getRelatedProducts())
                .map(relatedProducts -> relatedProducts.stream()
                        .map(this::toOverviewResponse)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    private List<Long> mapRelatedProductIdsFor(Product product) {
        return Optional.ofNullable(product.getRelatedProducts())
                .map(relatedProducts -> relatedProducts.stream()
                        .map(Product::getId)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private Set<AttributeResponse> mapAttributesFor(Product product) {
        return Optional.ofNullable(product.getAttributes())
                .map(attributes -> attributes.stream()
                        .map(attributeMapper::toResponse)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    private List<String> mapProductImages(Product product) {
        return Optional.ofNullable(product.getImages())
                .filter(images -> !images.isEmpty())
                .map(productImages -> productImages.stream()
                        .map(ProductImage::getImagePath)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    private String mapPrimaryImagePath(Product product) {
        return Optional.ofNullable(product.getImages())
                .filter(images -> !images.isEmpty())
                .map(images -> images.getFirst().getImagePath())
                .orElse(null);
    }
}

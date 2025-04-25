package com.ecommerce.product;

import com.ecommerce.variant.Variant;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.Set;

public class ProductSpecification implements Specification<Product> {
    private final BigDecimal lowPrice;
    private final BigDecimal maxPrice;
    private final Set<Long> attributeValueIds;

    public ProductSpecification(ProductSpecificationRequest request) {
        this.lowPrice = request.lowPrice();
        this.maxPrice = request.maxPrice();
        this.attributeValueIds = request.attributeValueIds();
    }

    @Override
    public @NonNull Predicate toPredicate(
            Root<Product> root,
            @NonNull CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder
    ) {
        Predicate predicate = criteriaBuilder.conjunction();
        Join<Product, Variant> productVariantJoin = root.join("variants");

        if (lowPrice != null) {
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.greaterThanOrEqualTo(productVariantJoin.get("discountedPrice"), lowPrice));
        }
        if (maxPrice != null) {
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.lessThanOrEqualTo(productVariantJoin.get("discountedPrice"), maxPrice));
        }

        if (attributeValueIds != null && !attributeValueIds.isEmpty()) {
            Join<Variant, String> variantAttributeValueJoin = productVariantJoin.join("values");
            predicate = criteriaBuilder.and(predicate,
                    variantAttributeValueJoin.get("id").in(attributeValueIds));
        }

        Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
        Root<Variant> variantRoot = subquery.from(Variant.class);
        subquery.select(criteriaBuilder.min(variantRoot.get("discountedPrice")));
        subquery.where(criteriaBuilder.equal(variantRoot.get("product"), root));

        return criteriaBuilder.and(predicate,
                criteriaBuilder.equal(productVariantJoin.get("discountedPrice"), subquery)
        );
    }
}


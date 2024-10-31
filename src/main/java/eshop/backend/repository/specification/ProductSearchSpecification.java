package eshop.backend.repository.specification;

import eshop.backend.model.AttributeValue;
import eshop.backend.model.Product;
import eshop.backend.model.Variant;
import eshop.backend.request.ProductSearchRequest;
import jakarta.persistence.criteria.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Set;

public class ProductSearchSpecification implements Specification<Product> {
    private final BigDecimal lowPrice;
    private final BigDecimal maxPrice;
    private final Set<Long> attributeValueIds;

    public ProductSearchSpecification(ProductSearchRequest request) {
        this.lowPrice = request.lowPrice();
        this.maxPrice = request.maxPrice();
        this.attributeValueIds = request.attributeValueIds();
    }

    @Override
    public Predicate toPredicate(@NotNull Root<Product> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder cb) {
        Join<Product, Variant> variantJoin = root.join("variants");

        Predicate predicate = cb.conjunction();

        if (lowPrice != null || maxPrice != null) {
            predicate = cb.and(predicate, cb.between(variantJoin.get("price"), lowPrice, maxPrice));
        }

        if (!attributeValueIds.isEmpty()) {
            Join<Variant, AttributeValue> attributeValueJoin = variantJoin.join("attributeValues");
            predicate = cb.and(predicate, attributeValueJoin.get("id").in(attributeValueIds));
        }

        return predicate;
    }
}
package com.ecommerce.product;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.variant.Variant;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductSpecificationTest {

    @Mock
    private Root<Product> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Join<Product, Variant> productVariantJoin;

    @Mock
    private Join<Variant, AttributeValue> variantAttributeValueJoin;

    @Mock
    private Subquery<BigDecimal> subquery;

    @Mock
    private Root<Variant> variantRoot;

    @Mock
    private Predicate predicate;

    @Mock
    private Predicate andPredicate;

    @Mock
    private Path<BigDecimal> pricePath;

    @Mock
    private Path<Long> idPath;

    private ProductSpecification specification;
    private ProductSpecificationRequest request;

    @BeforeEach
    void setUp() {
        // Setup common mock behaviors
        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(andPredicate);

        // Update to match the actual method call without JoinType parameter
        when(root.join("variants")).thenReturn((Join) productVariantJoin);

        // Fix the path generic types
        when(productVariantJoin.get("discountedPrice")).thenReturn((Path) pricePath);

        // Fix type casting for nested join
        when(productVariantJoin.join("values")).thenReturn((Join) variantAttributeValueJoin);
        when(variantAttributeValueJoin.get("id")).thenReturn((Path) idPath);

        when(query.subquery(BigDecimal.class)).thenReturn(subquery);
        when(subquery.from(Variant.class)).thenReturn(variantRoot);

        // Mock equal to return andPredicate for any arguments
        when(criteriaBuilder.equal(any(), any())).thenReturn(andPredicate);
    }

    @Test
    void toPredicate_WithNoFilters_ReturnsBasicPredicate() {
        // Arrange
        request = new ProductSpecificationRequest(null, null, null);
        specification = new ProductSpecification(request);

        when(criteriaBuilder.min(any())).thenReturn(mock(Expression.class));

        // Act
        specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(root).join("variants");
        verify(criteriaBuilder, never()).greaterThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        verify(criteriaBuilder, never()).lessThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        // Changed verification to atLeastOnce() since the method is called multiple times
        verify(criteriaBuilder, atLeastOnce()).equal(any(), any());
    }

    @Test
    void toPredicate_WithLowPrice_AppliesLowPriceFilter() {
        // Arrange
        BigDecimal lowPrice = new BigDecimal("10.00");
        request = new ProductSpecificationRequest(lowPrice, null, null);
        specification = new ProductSpecification(request);

        when(criteriaBuilder.greaterThanOrEqualTo(any(Expression.class), eq(lowPrice))).thenReturn(predicate);
        when(criteriaBuilder.min(any())).thenReturn(mock(Expression.class));

        // Act
        specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(root).join("variants");
        verify(criteriaBuilder).greaterThanOrEqualTo(any(Expression.class), eq(lowPrice));
        verify(criteriaBuilder, never()).lessThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        // Changed verification to atLeastOnce() since the method is called multiple times
        verify(criteriaBuilder, atLeastOnce()).equal(any(), any());
    }

    @Test
    void toPredicate_WithMaxPrice_AppliesMaxPriceFilter() {
        // Arrange
        BigDecimal maxPrice = new BigDecimal("100.00");
        request = new ProductSpecificationRequest(null, maxPrice, null);
        specification = new ProductSpecification(request);

        when(criteriaBuilder.lessThanOrEqualTo(any(Expression.class), eq(maxPrice))).thenReturn(predicate);
        when(criteriaBuilder.min(any())).thenReturn(mock(Expression.class));

        // Act
        specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(root).join("variants");
        verify(criteriaBuilder, never()).greaterThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        verify(criteriaBuilder).lessThanOrEqualTo(any(Expression.class), eq(maxPrice));
        // Changed verification to atLeastOnce() since the method is called multiple times
        verify(criteriaBuilder, atLeastOnce()).equal(any(), any());
    }

    @Test
    void toPredicate_WithAttributeValueIds_AppliesAttributeValueFilter() {
        // Arrange
        Set<Long> attributeValueIds = Set.of(1L, 2L);
        request = new ProductSpecificationRequest(null, null, attributeValueIds);
        specification = new ProductSpecification(request);

        when(criteriaBuilder.min(any())).thenReturn(mock(Expression.class));
        when(idPath.in(attributeValueIds)).thenReturn(predicate);

        // Act
        specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(root).join("variants");
        verify(productVariantJoin).join("values");
        verify(variantAttributeValueJoin).get("id");
        verify(idPath).in(attributeValueIds);
        verify(criteriaBuilder, never()).greaterThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        verify(criteriaBuilder, never()).lessThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        // Changed verification to atLeastOnce() since the method is called multiple times
        verify(criteriaBuilder, atLeastOnce()).equal(any(), any());
    }

    @Test
    void toPredicate_WithEmptyAttributeValueIds_DoesNotApplyAttributeValueFilter() {
        // Arrange
        request = new ProductSpecificationRequest(null, null, Collections.emptySet());
        specification = new ProductSpecification(request);

        when(criteriaBuilder.min(any())).thenReturn(mock(Expression.class));

        // Act
        specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(root).join("variants");
        verify(productVariantJoin, never()).join("values");
        verify(criteriaBuilder, never()).greaterThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        verify(criteriaBuilder, never()).lessThanOrEqualTo(any(Expression.class), any(BigDecimal.class));
        // Changed verification to atLeastOnce() since the method is called multiple times
        verify(criteriaBuilder, atLeastOnce()).equal(any(), any());
    }

    @Test
    void toPredicate_WithAllFilters_AppliesAllFilters() {
        // Arrange
        BigDecimal lowPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        Set<Long> attributeValueIds = Set.of(1L, 2L);
        request = new ProductSpecificationRequest(lowPrice, maxPrice, attributeValueIds);
        specification = new ProductSpecification(request);

        when(criteriaBuilder.greaterThanOrEqualTo(any(Expression.class), eq(lowPrice))).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(any(Expression.class), eq(maxPrice))).thenReturn(predicate);
        when(criteriaBuilder.min(any())).thenReturn(mock(Expression.class));
        when(idPath.in(attributeValueIds)).thenReturn(predicate);

        // Act
        specification.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).conjunction();
        verify(root).join("variants");
        verify(productVariantJoin).join("values");
        verify(variantAttributeValueJoin).get("id");
        verify(idPath).in(attributeValueIds);
        verify(criteriaBuilder).greaterThanOrEqualTo(any(Expression.class), eq(lowPrice));
        verify(criteriaBuilder).lessThanOrEqualTo(any(Expression.class), eq(maxPrice));
        // Changed verification to atLeastOnce() since the method is called multiple times
        verify(criteriaBuilder, atLeastOnce()).equal(any(), any());
    }
}
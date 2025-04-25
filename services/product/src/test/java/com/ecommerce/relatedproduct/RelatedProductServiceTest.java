package com.ecommerce.relatedproduct;

import com.ecommerce.product.Product;
import com.ecommerce.product.ProductRepository;
import com.ecommerce.product.ProductRequest;
import com.ecommerce.product.ProductValidator;
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.SelfRelatingProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatedProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductValidator productValidator;

    @InjectMocks
    private RelatedProductService relatedProductService;

    private Product testProduct;
    private ProductRequest testProductRequest;
    private Set<Long> relatedProductIds;
    private Product relatedProduct1;
    private Product relatedProduct2;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        relatedProduct1 = Product.builder()
                .id(2L)
                .name("Related Product 1")
                .build();

        relatedProduct2 = Product.builder()
                .id(3L)
                .name("Related Product 2")
                .build();

        relatedProductIds = Set.of(2L, 3L);

        testProductRequest = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),  // categoryIds
                Set.of(1L, 2L),  // attributeIds
                relatedProductIds
        );
    }

    @Test
    void processRelatedProducts_WithValidRelatedProductIds_ReturnsRelatedProducts() {
        // Arrange
        when(productRepository.findAllById(relatedProductIds)).thenReturn(List.of(relatedProduct1, relatedProduct2));
        doNothing().when(productValidator).validateNoSelfReference(relatedProductIds, testProduct.getId());
        doNothing().when(productValidator).validateAllProductsExist(relatedProductIds);

        // Act
        Set<Product> result = relatedProductService.processRelatedProducts(testProduct, testProductRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(relatedProduct1));
        assertTrue(result.contains(relatedProduct2));

        verify(productValidator).validateNoSelfReference(relatedProductIds, testProduct.getId());
        verify(productValidator).validateAllProductsExist(relatedProductIds);
        verify(productRepository).findAllById(relatedProductIds);
    }

    @Test
    void processRelatedProducts_WithNullRelatedProductIds_ReturnsEmptySet() {
        // Arrange
        ProductRequest requestWithNullIds = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                null  // null relatedProductIds
        );

        // Act
        Set<Product> result = relatedProductService.processRelatedProducts(testProduct, requestWithNullIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productValidator, never()).validateNoSelfReference(any(), any());
        verify(productValidator, never()).validateAllProductsExist(any());
        verify(productRepository, never()).findAllById(any());
    }

    @Test
    void processRelatedProducts_WithEmptyRelatedProductIds_ReturnsEmptySet() {
        // Arrange
        ProductRequest requestWithEmptyIds = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Collections.emptySet()  // empty relatedProductIds
        );

        // Act
        Set<Product> result = relatedProductService.processRelatedProducts(testProduct, requestWithEmptyIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productValidator, never()).validateNoSelfReference(any(), any());
        verify(productValidator, never()).validateAllProductsExist(any());
        verify(productRepository, never()).findAllById(any());
    }

    @Test
    void processRelatedProducts_WithNullProduct_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> relatedProductService.processRelatedProducts(null, testProductRequest));

        verify(productValidator, never()).validateNoSelfReference(any(), any());
        verify(productValidator, never()).validateAllProductsExist(any());
        verify(productRepository, never()).findAllById(any());
    }

    @Test
    void processRelatedProducts_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> relatedProductService.processRelatedProducts(testProduct, null));

        verify(productValidator, never()).validateNoSelfReference(any(), any());
        verify(productValidator, never()).validateAllProductsExist(any());
        verify(productRepository, never()).findAllById(any());
    }

    @Test
    void processRelatedProducts_WithSelfReference_ThrowsSelfRelatingProductException() {
        // Arrange
        doThrow(new SelfRelatingProductException("Product cannot reference itself"))
                .when(productValidator).validateNoSelfReference(relatedProductIds, testProduct.getId());

        // Act & Assert
        assertThrows(SelfRelatingProductException.class,
                () -> relatedProductService.processRelatedProducts(testProduct, testProductRequest));

        verify(productValidator).validateNoSelfReference(relatedProductIds, testProduct.getId());
        verify(productValidator, never()).validateAllProductsExist(any());
        verify(productRepository, never()).findAllById(any());
    }

    @Test
    void processRelatedProducts_WithNonExistentRelatedProduct_ThrowsProductNotFoundException() {
        // Arrange
        doThrow(new ProductNotFoundException("One or more related products do not exist"))
                .when(productValidator).validateAllProductsExist(relatedProductIds);

        // Act & Assert
        assertThrows(ProductNotFoundException.class,
                () -> relatedProductService.processRelatedProducts(testProduct, testProductRequest));

        verify(productValidator).validateNoSelfReference(relatedProductIds, testProduct.getId());
        verify(productValidator).validateAllProductsExist(relatedProductIds);
        verify(productRepository, never()).findAllById(any());
    }
}